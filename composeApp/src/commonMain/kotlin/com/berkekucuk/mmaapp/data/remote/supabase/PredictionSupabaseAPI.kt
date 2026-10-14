package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.PredictionRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.PredictionDto
import com.berkekucuk.mmaapp.data.remote.dto.PredictionInsertDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class PredictionSupabaseAPI(
    private val client: SupabaseClient
) : PredictionRemoteDataSource {

    override suspend fun fetchPredictions(userId: String, limit: Int, offset: Int): List<PredictionDto> {
        return client.postgrest.rpc(
            function = "get_user_predictions",
            parameters = buildJsonObject {
                put("p_user_id", userId)
                put("p_limit", limit)
                put("p_offset", offset)
            }
        ).decodeList<PredictionDto>()
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
