package com.berkekucuk.mmaapp.data.repository

import com.berkekucuk.mmaapp.core.utils.RateLimiter
import com.berkekucuk.mmaapp.data.local.dao.FighterDao
import com.berkekucuk.mmaapp.data.local.dao.InteractionDao
import com.berkekucuk.mmaapp.data.mapper.toDomain
import com.berkekucuk.mmaapp.data.mapper.toEntity
import com.berkekucuk.mmaapp.data.remote.datasource.InteractionRemoteDataSource
import com.berkekucuk.mmaapp.domain.model.Fighter
import com.berkekucuk.mmaapp.domain.model.Interaction
import com.berkekucuk.mmaapp.domain.repository.InteractionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.collections.map
import kotlin.coroutines.cancellation.CancellationException

class InteractionRepositoryImpl(
    private val remoteDataSource: InteractionRemoteDataSource,
    private val interactionDao: InteractionDao,
    private val fighterDao: FighterDao,
    private val rateLimiter: RateLimiter
) : InteractionRepository {

    private fun syncKey(userId: String) = "sync_interactions_$userId"

    override fun getInteractions(userId: String, type: String?): Flow<List<Interaction>> {
        return interactionDao.getInteractions(userId, type)
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun syncInteractions(userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                if (!rateLimiter.shouldFetch(syncKey(userId))) {
                    return@runCatching
                }
                val remoteInteractions = remoteDataSource.fetchInteractions(userId)
                val remoteFighters = remoteInteractions.mapNotNull { it.fighter }

                if (remoteFighters.isNotEmpty()) {
                    fighterDao.upsertFighters(remoteFighters.map { it.toEntity() })
                }
                interactionDao.replaceInteractions(userId, remoteInteractions.map { it.toEntity() })
            }.onFailure {
                if (it is CancellationException) throw it
                rateLimiter.reset(syncKey(userId))
            }
        }
    }

    override suspend fun addInteraction(
        userId: String,
        interactionType: String,
        fighter: Fighter,
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val remoteInteraction = remoteDataSource.addInteraction(userId, fighter.fighterId, interactionType)

                fighterDao.insertFighter(fighter.toEntity())

                interactionDao.upsertInteractions(listOf(remoteInteraction.toEntity()))
            }.onFailure {
                if (it is CancellationException) throw it
            }
        }
    }

    override suspend fun removeInteraction(interactionId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val localInteraction = interactionDao.getInteraction(interactionId)
                
                remoteDataSource.removeInteraction(interactionId)

                localInteraction?.let {
                    interactionDao.decrementRanksAbove(
                        userId = it.userId,
                        type = it.interactionType,
                        removedRank = it.rankNumber
                    )
                }
                interactionDao.deleteInteraction(interactionId)
            }.onFailure {
                if (it is CancellationException) throw it
            }
        }
    }
}