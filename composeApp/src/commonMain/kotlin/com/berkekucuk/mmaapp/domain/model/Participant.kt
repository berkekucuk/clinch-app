package com.berkekucuk.mmaapp.domain.model

import androidx.compose.runtime.Immutable
import com.berkekucuk.mmaapp.domain.enums.Result

@Immutable
data class Participant(
    val result: Result,
    val recordAfterFight: Record?,
    val isRedCorner: Boolean,
    val fighter: Fighter
)
