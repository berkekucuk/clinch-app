package com.berkekucuk.mmaapp.domain.repository

import com.berkekucuk.mmaapp.domain.model.Prediction
import kotlinx.coroutines.flow.Flow

interface PredictionRepository {
    fun getPredictedWinnerId(fightId: String, userId: String): Flow<String?>
    fun getPredictions(userId: String, limit: Int, offset: Int): Flow<List<Prediction>>
    suspend fun addPrediction(userId: String, fightId: String, predictedWinnerId: String, lockedOdds: Int, selectedRisk: Int): Result<Unit>
    suspend fun syncPredictions(userId: String, limit: Int, offset: Int): Result<Unit>
}
