package com.berkekucuk.mmaapp.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class FightStat(
    val round: Int,
    val fighterId: String,
    val knockdowns: Int,
    val sigStrikesLanded: Int,
    val sigStrikesAttempted: Int,
    val totalStrikesLanded: Int,
    val totalStrikesAttempted: Int,
    val takedownsLanded: Int,
    val takedownsAttempted: Int,
    val submissionAttempts: Int,
    val reversals: Int,
    val controlTimeSeconds: Int,
    val headLanded: Int,
    val headAttempted: Int,
    val bodyLanded: Int,
    val bodyAttempted: Int,
    val legLanded: Int,
    val legAttempted: Int,
    val distanceLanded: Int,
    val distanceAttempted: Int,
    val clinchLanded: Int,
    val clinchAttempted: Int,
    val groundLanded: Int,
    val groundAttempted: Int
)
