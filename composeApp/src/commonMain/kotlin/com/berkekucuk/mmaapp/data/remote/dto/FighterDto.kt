package com.berkekucuk.mmaapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FighterDto(
    @SerialName("fighter_id") val fighterId: String,
    val name: String? = null,
    val nickname: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    val record: RecordDto? = null,
    val height: MeasurementDto? = null,
    val reach: MeasurementDto? = null,
    @SerialName("weight_class_id") val weightClassId: String? = null,
    @SerialName("date_of_birth") val dateOfBirth: String? = null,
    val born: String? = null,
    @SerialName("fighting_out_of") val fightingOutOf: String? = null,
    @SerialName("country_code") val countryCode: String? = null,
    @SerialName("win_rate") val winRate: Float? = null,
    @SerialName("ko_tko_rate") val koTkoRate: Float? = null,
    @SerialName("submission_rate") val submissionRate: Float? = null,
    val fights: List<FightDto>? = null
)