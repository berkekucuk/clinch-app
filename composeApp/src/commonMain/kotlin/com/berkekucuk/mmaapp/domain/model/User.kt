package com.berkekucuk.mmaapp.domain.model

import androidx.compose.runtime.Immutable
import kotlin.math.roundToInt
import kotlin.time.Instant

@Immutable
data class User(
    val id: String,
    val username: String?,
    val fullName: String?,
    val avatarUrl: String?,
    val points: Int,
    val createdAt: Instant?,
    val correctCount: Int = 0,
    val decidedCount: Int = 0,
) {
    val accuracyPercent: Int? =
        if (decidedCount > 0) (correctCount * 100.0 / decidedCount).roundToInt() else null
}
