package com.berkekucuk.mmaapp.data.remote.datasource

import com.berkekucuk.mmaapp.data.remote.dto.PredictionDto

interface PredictionRemoteDataSource {
    suspend fun fetchPredictions(userId: String, limit: Int, offset: Int): List<PredictionDto>
    suspend fun addPrediction(fightId: String, predictedWinnerId: String, method: String?, round: Int?): PredictionDto
}
