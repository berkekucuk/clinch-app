package com.berkekucuk.mmaapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManualNotificationDto(
    val action: String = "manual_notification",
    val title: String,
    val body: String,
    val target: String,
    @SerialName("user_ids") val userIds: List<String>? = null,
    val data: Map<String, String>? = null,
)
