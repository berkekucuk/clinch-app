package com.berkekucuk.mmaapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FightNotificationDto(
    @SerialName("user_id") val userId: String,
    @SerialName("fight_id") val fightId: String,
    @SerialName("is_alarm") val isAlarm: Boolean? = false,
)
