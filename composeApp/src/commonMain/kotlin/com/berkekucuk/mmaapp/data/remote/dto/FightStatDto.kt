package com.berkekucuk.mmaapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FightStatDto(
    val round: Int,
    @SerialName("fighter_id") val fighterId: String,
    val knockdowns: Int? = null,
    @SerialName("sig_strikes_landed") val sigStrikesLanded: Int? = null,
    @SerialName("sig_strikes_attempted") val sigStrikesAttempted: Int? = null,
    @SerialName("total_strikes_landed") val totalStrikesLanded: Int? = null,
    @SerialName("total_strikes_attempted") val totalStrikesAttempted: Int? = null,
    @SerialName("takedowns_landed") val takedownsLanded: Int? = null,
    @SerialName("takedowns_attempted") val takedownsAttempted: Int? = null,
    @SerialName("submission_attempts") val submissionAttempts: Int? = null,
    val reversals: Int? = null,
    @SerialName("control_time_seconds") val controlTimeSeconds: Int? = null,
    @SerialName("head_landed") val headLanded: Int? = null,
    @SerialName("head_attempted") val headAttempted: Int? = null,
    @SerialName("body_landed") val bodyLanded: Int? = null,
    @SerialName("body_attempted") val bodyAttempted: Int? = null,
    @SerialName("leg_landed") val legLanded: Int? = null,
    @SerialName("leg_attempted") val legAttempted: Int? = null,
    @SerialName("distance_landed") val distanceLanded: Int? = null,
    @SerialName("distance_attempted") val distanceAttempted: Int? = null,
    @SerialName("clinch_landed") val clinchLanded: Int? = null,
    @SerialName("clinch_attempted") val clinchAttempted: Int? = null,
    @SerialName("ground_landed") val groundLanded: Int? = null,
    @SerialName("ground_attempted") val groundAttempted: Int? = null
)
