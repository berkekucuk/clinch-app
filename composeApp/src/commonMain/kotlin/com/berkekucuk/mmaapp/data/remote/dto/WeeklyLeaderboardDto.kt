package com.berkekucuk.mmaapp.data.remote.dto

import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeeklyLeaderboardDto(
    @SerialName("event_id") val eventId: String,
    @SerialName("user_id") val userId: String,
    val username: String? = null,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("weekly_points") val weeklyPoints: Int,
    @SerialName("created_at") val createdAt: Instant? = null
)
