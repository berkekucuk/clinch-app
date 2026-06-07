package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.PredictionRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.PredictionDto
import com.berkekucuk.mmaapp.data.remote.dto.PredictionInsertDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class PredictionSupabaseAPI(
    private val client: SupabaseClient
) : PredictionRemoteDataSource {

    override suspend fun fetchPredictions(userId: String, limit: Int, offset: Int): List<PredictionDto> {
        return client.from("prediction_view_v3").select {
            filter {
                eq("user_id", userId)
            }
            range(offset.toLong(), (offset + limit - 1).toLong())
        }.decodeList<PredictionDto>()
    }

    override suspend fun addPrediction(
        userId: String,
        fightId: String,
        predictedWinnerId: String,
        lockedOdds: Int,
        selectedRisk: Int
    ): PredictionDto {
        val request = PredictionInsertDto(userId, fightId, predictedWinnerId, lockedOdds, selectedRisk)
        return client.from("user_predictions").insert(request) {
            select()
        }.decodeSingle<PredictionDto>()
    }
}
