package com.berkekucuk.mmaapp.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class WeightClass(
    val id: String,
    val sortOrder: Int,
    val rankings: List<RankedFighter>
) {
    val isWomens: Boolean = id in setOf("womens_p4p", "SW", "W_FLW", "W_BW")
}
