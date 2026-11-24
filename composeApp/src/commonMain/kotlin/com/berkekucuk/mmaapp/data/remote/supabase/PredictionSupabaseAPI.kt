package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.PredictionRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.PredictionDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class PredictionSupabaseAPI(
    private val client: SupabaseClient
) : PredictionRemoteDataSource {

    override suspend fun fetchPredictions(userId: String, limit: Int, offset: Int): List<PredictionDto> {
        return client.postgrest.rpc(
            function = "get_user_predictions_v3",
            parameters = buildJsonObject {
                put("p_user_id", userId)
                put("p_limit", limit)
                put("p_offset", offset)
            }
        ).decodeList<PredictionDto>()
    }

    override suspend fun addPrediction(fightId: String, predictedWinnerId: String, method: String?, round: Int?): PredictionDto {
        return client.postgrest.rpc(
            function = "add_prediction",
            parameters = buildJsonObject {
                put("p_fight_id", fightId)
                put("p_predicted_winner_id", predictedWinnerId)
                method?.let { put("p_predicted_method", it) }
                round?.let { put("p_predicted_round", it) }
            }
        ).decodeList<PredictionDto>().first()
    }
}
