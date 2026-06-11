package com.berkekucuk.mmaapp.domain.model

data class RankedFighter(
    val rankNumber: Int,
    val rankChange: Int? = null,
    val fighter: Fighter?,
) {
    val isChampion: Boolean get() = rankNumber == 0
}