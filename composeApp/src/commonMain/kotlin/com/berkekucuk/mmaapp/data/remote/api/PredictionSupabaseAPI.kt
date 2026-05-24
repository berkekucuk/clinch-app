package com.berkekucuk.mmaapp.data.remote.api

import com.berkekucuk.mmaapp.data.remote.dto.PredictionDto
import com.berkekucuk.mmaapp.data.remote.dto.PredictionInsertDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class PredictionSupabaseAPI(
    private val client: SupabaseClient
) : PredictionRemoteDataSource {

    override suspend fun fetchPredictions(userId: String, limit: Int, offset: Int): List<PredictionDto> {
        return client.from("prediction_view_v2").select {
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
        lockedOdds: Int
    ): PredictionDto {
        val request = PredictionInsertDto(userId, fightId, predictedWinnerId, lockedOdds)
        return client.from("user_predictions").insert(request) {
            select()
        }.decodeSingle<PredictionDto>()
    }
}
