package com.berkekucuk.mmaapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParticipantDto(
    val result: String? = null,
    @SerialName("record_after_fight") val recordAfterFight: RecordDto? = null,
    @SerialName("is_red_corner") val isRedCorner: Boolean? = null,
    @SerialName("fighters") val fighter: FighterDto? = null
)