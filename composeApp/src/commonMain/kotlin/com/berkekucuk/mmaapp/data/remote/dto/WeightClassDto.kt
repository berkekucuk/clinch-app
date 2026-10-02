package com.berkekucuk.mmaapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeightClassDto(
    @SerialName("id") val id: String,
    @SerialName("sort_order") val sortOrder: Int? = null,
    @SerialName("weight_limit") val weightLimit: Int? = null,
    @SerialName("rankings") val rankings: List<RankedFighterDto>? = null
)