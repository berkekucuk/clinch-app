package com.berkekucuk.mmaapp.data.repository

import com.berkekucuk.mmaapp.core.utils.RateLimiter
import com.berkekucuk.mmaapp.data.local.dao.FightDao
import com.berkekucuk.mmaapp.data.local.dao.FightStatDao
import com.berkekucuk.mmaapp.data.mapper.toDomain
import com.berkekucuk.mmaapp.data.mapper.toEntity
import com.berkekucuk.mmaapp.data.remote.datasource.FightRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.FightDto
import com.berkekucuk.mmaapp.domain.model.Fight
import com.berkekucuk.mmaapp.domain.repository.FightRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class FightRepositoryImpl(
    private val fightDao: FightDao,
    private val fightStatDao: FightStatDao,
    private val remoteDataSource: FightRemoteDataSource,
    private val rateLimiter: RateLimiter
) : FightRepository {
    
    private fun syncKey(fightId: String) = "sync_fight_$fightId"

    override fun getFight(fightId: String): Flow<Fight> {
        return fightDao.getFight(fightId)
            .filterNotNull()
            .map { it.toDomain() }
            .flowOn(Dispatchers.IO)
            .distinctUntilChanged()
    }

    override suspend fun syncFight(fightId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                if (!rateLimiter.shouldFetch(syncKey(fightId))) {
                    return@runCatching
                }

                val fightDto = remoteDataSource.fetchFight(fightId)
                saveFightAndStats(fightDto)
            }.onFailure {
                if (it is CancellationException) throw it
                rateLimiter.reset(syncKey(fightId))
            }
        }
    }

    private suspend fun saveFightAndStats(remoteFight: FightDto) {
        // 1. Save fight to centralized fights table
        fightDao.upsertFights(listOf(remoteFight.toEntity()))

        // 2. Save stats to dedicated fight_stats table
        val stats = remoteFight.stats ?: emptyList()
        val statEntities = stats.map { it.toEntity(remoteFight.fightId) }
        fightStatDao.replaceStatsForFight(remoteFight.fightId, statEntities)
    }
}
