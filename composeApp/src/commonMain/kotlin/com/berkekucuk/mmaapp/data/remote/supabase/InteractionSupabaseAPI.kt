package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.InteractionRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.InteractionDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class InteractionSupabaseAPI(
    private val client: SupabaseClient
) : InteractionRemoteDataSource {

    override suspend fun fetchInteractions(userId: String): List<InteractionDto> {
        return client.postgrest.rpc(
            function = "get_user_interactions",
            parameters = buildJsonObject {
                put("p_user_id", userId)
            }
        ).decodeList<InteractionDto>()
    }

    override suspend fun addInteraction(fighterId: String, interactionType: String): InteractionDto {
        return client.postgrest.rpc(
            function = "add_fighter_interaction",
            parameters = buildJsonObject {
                put("p_fighter_id", fighterId)
                put("p_interaction_type", interactionType)
            }
        ).decodeList<InteractionDto>().first()
    }

    override suspend fun removeInteraction(interactionId: String) {
        client.postgrest.rpc(
            function = "remove_fighter_interaction",
            parameters = buildJsonObject {
                put("p_interaction_id", interactionId)
            }
        )
    }
}
