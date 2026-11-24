package com.berkekucuk.mmaapp.data.remote.dto

import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PredictionDto(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("fight_id") val fightId: String,
    @SerialName("predicted_winner_id") val predictedWinnerId: String? = null,
    @SerialName("points_earned") val pointsEarned: Int? = null,
    @SerialName("is_correct") val isCorrect: Boolean? = null,
    @SerialName("locked_odds") val lockedOdds: Int? = null,
    @SerialName("predicted_method") val predictedMethod: String? = null,
    @SerialName("predicted_round") val predictedRound: Int? = null,
    @SerialName("created_at") val createdAt: Instant? = null,
    @SerialName("fight") val fight: FightDto? = null
)