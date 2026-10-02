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
    val slpm: Float? = null,
    @SerialName("str_acc") val strAcc: Float? = null,
    val sapm: Float? = null,
    @SerialName("str_def") val strDef: Float? = null,
    @SerialName("td_avg") val tdAvg: Float? = null,
    @SerialName("td_acc") val tdAcc: Float? = null,
    @SerialName("td_def") val tdDef: Float? = null,
    @SerialName("sub_avg") val subAvg: Float? = null,
    val fights: List<FightDto>? = null
)