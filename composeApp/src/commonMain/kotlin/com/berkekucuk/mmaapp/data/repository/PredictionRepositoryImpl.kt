package com.berkekucuk.mmaapp.data.repository

import com.berkekucuk.mmaapp.core.utils.RateLimiter
import com.berkekucuk.mmaapp.data.local.dao.FightDao
import com.berkekucuk.mmaapp.data.local.dao.PredictionDao
import com.berkekucuk.mmaapp.data.remote.api.PredictionRemoteDataSource
import com.berkekucuk.mmaapp.domain.repository.PredictionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import com.berkekucuk.mmaapp.data.mapper.toDomain
import com.berkekucuk.mmaapp.data.mapper.toEntity
import com.berkekucuk.mmaapp.domain.model.Prediction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PredictionRepositoryImpl(
    private val predictionDao: PredictionDao,
    private val fightDao: FightDao,
    private val remoteDataSource: PredictionRemoteDataSource,
    private val rateLimiter: RateLimiter
) : PredictionRepository {

    private fun syncKey(userId: String) = "sync_predictions_$userId"

    override fun getPredictedWinnerId(fightId: String, userId: String): Flow<String?> {
        return predictionDao.getPredictedWinnerId(fightId, userId)
    }

    override fun getPredictions(userId: String): Flow<List<Prediction>> {
        return predictionDao.getPredictions(userId)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override suspend fun syncPredictions(userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                if (!rateLimiter.shouldFetch(syncKey(userId))) {
                    return@runCatching
                }
                val remotePredictions = remoteDataSource.fetchPredictions(userId)
                val remoteFights = remotePredictions.mapNotNull { it.fight }

                if (remoteFights.isNotEmpty()) {
                    fightDao.upsertFights(remoteFights.map { it.toEntity() })
                }
                predictionDao.replacePredictions(userId, remotePredictions.map { it.toEntity() })
            }.onFailure {
                if (it is CancellationException) throw it
                rateLimiter.reset(syncKey(userId))
            }
        }
    }

    override suspend fun addPrediction(
        userId: String,
        fightId: String,
        predictedWinnerId: String,
        lockedOdds: Int
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val remotePrediction = remoteDataSource.addPrediction(userId, fightId, predictedWinnerId, lockedOdds)

                predictionDao.upsertPredictions(listOf(remotePrediction.toEntity()))
            }.onFailure {
                if (it is CancellationException) throw it
            }
        }
    }
}