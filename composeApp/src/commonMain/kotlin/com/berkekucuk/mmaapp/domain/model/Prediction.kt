package com.berkekucuk.mmaapp.domain.model

import kotlin.time.Instant

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
    val isCancelledOrFizzled: Boolean
        get() = fight?.isCancelledOrFizzled == true
}