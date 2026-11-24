package com.berkekucuk.mmaapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FightOddsDto(
    @SerialName("fighter_id") val fighterId: String,
    @SerialName("method") val method: String,
    @SerialName("round") val round: Int = 0,
    @SerialName("odds_value") val oddsValue: Int,
)
