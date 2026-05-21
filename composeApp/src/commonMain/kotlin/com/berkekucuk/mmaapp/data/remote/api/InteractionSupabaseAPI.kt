package com.berkekucuk.mmaapp.data.remote.api

import com.berkekucuk.mmaapp.data.remote.dto.InteractionDto
import com.berkekucuk.mmaapp.data.remote.dto.FighterInteractionInsertDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class InteractionSupabaseAPI(
    private val client: SupabaseClient
) : InteractionRemoteDataSource {

    override suspend fun fetchInteractions(userId: String): List<InteractionDto> {
        return client.from("fighter_interaction_view").select {
            filter {
                eq("user_id", userId)
            }
        }.decodeList<InteractionDto>()
    }

    override suspend fun addInteraction(
        userId: String,
        fighterId: String,
        interactionType: String
    ): InteractionDto {
        val request = FighterInteractionInsertDto(userId, fighterId, interactionType)

        return client.from("user_fighter_interactions").insert(request) {
            select()
        }.decodeSingle<InteractionDto>()
    }

    override suspend fun removeInteraction(interactionId: String) {
        client.from("user_fighter_interactions").delete {
            filter {
                eq("id", interactionId)
            }
        }
    }
}
