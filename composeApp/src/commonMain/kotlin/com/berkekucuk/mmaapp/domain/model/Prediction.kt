package com.berkekucuk.mmaapp.domain.model

import androidx.compose.runtime.Immutable
import kotlin.time.Instant

@Immutable
data class Prediction(
    val predictionId: String,
    val fightId: String,
    val userId: String,
    val predictedWinnerId: String,
    val pointsEarned: Int,
    val isCorrect: Boolean?,
    val lockedOdds: Int?,
    val createdAt: Instant,
    val fight: Fight?
) {
    val isCancelledOrFizzled: Boolean = fight?.isCancelledOrFizzled == true
}