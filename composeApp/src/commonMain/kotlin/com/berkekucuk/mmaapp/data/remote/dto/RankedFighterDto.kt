package com.berkekucuk.mmaapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RankedFighterDto(
    @SerialName("rank_number") val rankNumber: Int,
    @SerialName("fighters") val fighter: FighterDto? = null,
    @SerialName("rank_change") val rankChange: Int? = null
)