package com.berkekucuk.mmaapp.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class RankedFighter(
    val rankNumber: Int,
    val rankChange: Int? = null,
    val fighter: Fighter?,
) {
    val isChampion: Boolean = rankNumber == 0
}