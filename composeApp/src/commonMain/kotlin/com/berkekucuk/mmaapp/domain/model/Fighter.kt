package com.berkekucuk.mmaapp.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Fighter(
    val fighterId: String,
    val name: String,
    val nickname: String?,
    val imageUrl: String,
    val record: Record,
    val height: Measurement,
    val reach: Measurement,
    val weightClassId: String,
    val dateOfBirth: String,
    val born: String?,
    val fightingOutOf: String?,
    val countryCode: String,
    val winRate: Float,
    val koTkoRate: Float,
    val submissionRate: Float,
    val slpm: Float,
    val strAcc: Float,
    val sapm: Float,
    val strDef: Float,
    val tdAvg: Float,
    val tdAcc: Float,
    val tdDef: Float,
    val subAvg: Float,
    val fights: List<Fight> = emptyList()
)
