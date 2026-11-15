package com.berkekucuk.mmaapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PredictionInsertDto(
    @SerialName("user_id") val userId: String,
    @SerialName("fight_id") val fightId: String,
    @SerialName("predicted_winner_id") val predictedWinnerId: String,
    @SerialName("locked_odds") val lockedOdds: Int
)