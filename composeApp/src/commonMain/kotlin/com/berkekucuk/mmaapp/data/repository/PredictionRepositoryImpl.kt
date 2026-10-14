package com.berkekucuk.mmaapp.data.repository

import com.berkekucuk.mmaapp.core.utils.runSuspendCatching
import com.berkekucuk.mmaapp.core.utils.RateLimiter
import com.berkekucuk.mmaapp.data.local.dao.FightDao
import com.berkekucuk.mmaapp.data.local.dao.PredictionDao
import com.berkekucuk.mmaapp.data.remote.datasource.PredictionRemoteDataSource
import com.berkekucuk.mmaapp.domain.repository.PredictionRepository
import kotlinx.coroutines.Dispatchers
import com.berkekucuk.mmaapp.data.mapper.toDomain
import com.berkekucuk.mmaapp.data.mapper.toEntity
import com.berkekucuk.mmaapp.domain.model.Prediction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class PredictionRepositoryImpl(
    private val predictionDao: PredictionDao,
    private val fightDao: FightDao,
    private val remoteDataSource: PredictionRemoteDataSource,
    private val rateLimiter: RateLimiter
) : PredictionRepository {

    private fun syncKey(userId: String, limit: Int, offset: Int) = "sync_predictions_${userId}_limit_${limit}_offset_${offset}"

    override fun getPredictedWinnerId(fightId: String, userId: String): Flow<String?> {
        return predictionDao.getPredictedWinnerId(fightId, userId)
            .distinctUntilChanged()
    }

    override fun getPredictions(userId: String, limit: Int, offset: Int): Flow<List<Prediction>> {
        return predictionDao.getPredictions(userId, limit, offset)
            .map { entities ->
                entities.map { it.toDomain() }
            }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)
    }

    override suspend fun syncPredictions(userId: String, limit: Int, offset: Int): Result<Unit> {
        val key = syncKey(userId, limit, offset)
        return runSuspendCatching(Dispatchers.Default) {
            if (!rateLimiter.shouldFetch(key)) {
                return@runSuspendCatching
            }

            val remotePredictions = remoteDataSource.fetchPredictions(userId, limit, offset)

            if (remotePredictions.isNotEmpty()) {
                val remoteFights = remotePredictions.mapNotNull { it.fight }
                if (remoteFights.isNotEmpty()) {
                    fightDao.insertFightsIfNotExist(remoteFights.map { it.toEntity() })
                }
                predictionDao.upsertPredictions(remotePredictions.map { it.toEntity() })
            }
        }.onFailure {
            rateLimiter.reset(key)
        }
    }

    override suspend fun addPrediction(
        userId: String,
        fightId: String,
        predictedWinnerId: String,
        lockedOdds: Int,
        selectedRisk: Int
    ): Result<Unit> {
        return runSuspendCatching(Dispatchers.Default) {
            val remotePrediction = remoteDataSource.addPrediction(userId, fightId, predictedWinnerId, lockedOdds, selectedRisk)

            predictionDao.upsertPredictions(listOf(remotePrediction.toEntity()))
        }
    }
}