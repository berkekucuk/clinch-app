package com.berkekucuk.mmaapp.data.repository

import com.berkekucuk.mmaapp.core.utils.RateLimiter
import com.berkekucuk.mmaapp.data.local.dao.FightDao
import com.berkekucuk.mmaapp.data.local.dao.FighterDao
import com.berkekucuk.mmaapp.data.mapper.toDomain
import com.berkekucuk.mmaapp.data.mapper.toEntity
import com.berkekucuk.mmaapp.data.remote.datasource.FightRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.datasource.FighterRemoteDataSource
import com.berkekucuk.mmaapp.domain.model.Fighter
import com.berkekucuk.mmaapp.domain.repository.FighterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import com.berkekucuk.mmaapp.data.local.entity.FighterFightCrossRef
import com.berkekucuk.mmaapp.data.remote.dto.FightDto
import com.berkekucuk.mmaapp.data.remote.dto.FighterDto

class FighterRepositoryImpl(
    private val fighterRemoteDataSource: FighterRemoteDataSource,
    private val fightRemoteDataSource: FightRemoteDataSource,
    private val fighterDao: FighterDao,
    private val fightDao: FightDao,
    private val rateLimiter: RateLimiter
) : FighterRepository {

    private fun syncKey(fighterId: String) = "sync_fighter_$fighterId"

    override fun getFighter(fighterId: String): Flow<Fighter> {
        return fighterDao.getFighter(fighterId)
            .filterNotNull()
            .map { it.toDomain() }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun syncFighter(fighterId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                if (!rateLimiter.shouldFetch(syncKey(fighterId))) {
                    return@runCatching
                }
                val remoteFighter = fighterRemoteDataSource.fetchFighter(fighterId)
                val remoteFights = fightRemoteDataSource.fetchFightsByFighter(fighterId)
                saveFighterAndFights(remoteFighter, remoteFights)
            }.onFailure {
                if (it is CancellationException) throw it
                rateLimiter.reset(syncKey(fighterId))
            }
        }
    }

    private suspend fun saveFighterAndFights(remoteFighter: FighterDto, fights: List<FightDto>) {
        // 1. Save fighter to local database
        fighterDao.upsertFighters(listOf(remoteFighter.toEntity()))
        
        // 2. Save fights to centralized table
        val fightEntities = fights.map { it.toEntity() }
        if (fightEntities.isNotEmpty()) {
            fightDao.upsertFights(fightEntities)
        }
        
        // 3. Update Junction table (Fighter <-> Fights)
        fighterDao.deleteFighterFightCrossRefs(remoteFighter.fighterId)
        val crossRefs = fights.map { 
            FighterFightCrossRef(fighterId = remoteFighter.fighterId, fightId = it.fightId)
        }
        if (crossRefs.isNotEmpty()) {
            fighterDao.upsertFighterFightCrossRefs(crossRefs)
        }
    }

    override suspend fun searchFighters(query: String): Result<List<Fighter>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                fighterRemoteDataSource.searchFighters(query)
                    .map { it.toDomain() }
            }.onFailure {
                if (it is CancellationException) throw it
            }
        }
    }
}
