package com.berkekucuk.mmaapp.data.remote.dto

import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventCommentDto(
    val id: String,
    @SerialName("event_id") val eventId: String,
    @SerialName("user_id") val userId: String,
    val content: String,
    @SerialName("created_at") val createdAt: Instant,
    val username: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
)

@Serializable
data class EventCommentDeletedDto(
    val id: String,
)
