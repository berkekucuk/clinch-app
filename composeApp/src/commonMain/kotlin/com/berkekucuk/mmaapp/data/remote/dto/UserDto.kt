package com.berkekucuk.mmaapp.data.remote.dto

import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val username: String? = null,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("points") val points: Int? = 0,
    @SerialName("created_at") val createdAt: Instant? = null,
)