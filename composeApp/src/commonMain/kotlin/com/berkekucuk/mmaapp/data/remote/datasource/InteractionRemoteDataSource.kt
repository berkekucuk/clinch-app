package com.berkekucuk.mmaapp.data.remote.datasource

import com.berkekucuk.mmaapp.data.remote.dto.InteractionDto

interface InteractionRemoteDataSource {
    suspend fun fetchInteractions(userId: String): List<InteractionDto>
    suspend fun addInteraction(fighterId: String, interactionType: String): InteractionDto
    suspend fun removeInteraction(interactionId: String)
}
