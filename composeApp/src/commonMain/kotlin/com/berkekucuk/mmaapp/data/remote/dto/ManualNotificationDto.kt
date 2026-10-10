package com.berkekucuk.mmaapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ManualNotificationDto(
    val action: String = "manual_notification",
    val title: String,
    val body: String,
    val target: String,
    val usernames: List<String>? = null,
    val data: Map<String, String>? = null,
)
