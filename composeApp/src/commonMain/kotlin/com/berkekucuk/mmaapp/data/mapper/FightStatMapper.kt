package com.berkekucuk.mmaapp.data.mapper

import com.berkekucuk.mmaapp.data.local.entity.FightStatEntity
import com.berkekucuk.mmaapp.data.remote.dto.FightStatDto
import com.berkekucuk.mmaapp.domain.model.FightStat

fun FightStatDto.toEntity(fightId: String): FightStatEntity {
    return FightStatEntity(
        fightId = fightId,
        fighterId = fighterId,
        round = round,
        knockdowns = knockdowns ?: 0,
        sigStrikesLanded = sigStrikesLanded ?: 0,
        sigStrikesAttempted = sigStrikesAttempted ?: 0,
        totalStrikesLanded = totalStrikesLanded ?: 0,
        totalStrikesAttempted = totalStrikesAttempted ?: 0,
        takedownsLanded = takedownsLanded ?: 0,
        takedownsAttempted = takedownsAttempted ?: 0,
        submissionAttempts = submissionAttempts ?: 0,
        reversals = reversals ?: 0,
        controlTimeSeconds = controlTimeSeconds ?: 0,
        headLanded = headLanded ?: 0,
        headAttempted = headAttempted ?: 0,
        bodyLanded = bodyLanded ?: 0,
        bodyAttempted = bodyAttempted ?: 0,
        legLanded = legLanded ?: 0,
        legAttempted = legAttempted ?: 0,
        distanceLanded = distanceLanded ?: 0,
        distanceAttempted = distanceAttempted ?: 0,
        clinchLanded = clinchLanded ?: 0,
        clinchAttempted = clinchAttempted ?: 0,
        groundLanded = groundLanded ?: 0,
        groundAttempted = groundAttempted ?: 0
    )
}

fun FightStatEntity.toDomain(): FightStat {
    return FightStat(
        round = round,
        fighterId = fighterId,
        knockdowns = knockdowns ?: 0,
        sigStrikesLanded = sigStrikesLanded ?: 0,
        sigStrikesAttempted = sigStrikesAttempted ?: 0,
        totalStrikesLanded = totalStrikesLanded ?: 0,
        totalStrikesAttempted = totalStrikesAttempted ?: 0,
        takedownsLanded = takedownsLanded ?: 0,
        takedownsAttempted = takedownsAttempted ?: 0,
        submissionAttempts = submissionAttempts ?: 0,
        reversals = reversals ?: 0,
        controlTimeSeconds = controlTimeSeconds ?: 0,
        headLanded = headLanded ?: 0,
        headAttempted = headAttempted ?: 0,
        bodyLanded = bodyLanded ?: 0,
        bodyAttempted = bodyAttempted ?: 0,
        legLanded = legLanded ?: 0,
        legAttempted = legAttempted ?: 0,
        distanceLanded = distanceLanded ?: 0,
        distanceAttempted = distanceAttempted ?: 0,
        clinchLanded = clinchLanded ?: 0,
        clinchAttempted = clinchAttempted ?: 0,
        groundLanded = groundLanded ?: 0,
        groundAttempted = groundAttempted ?: 0
    )
}

fun FightStatDto.toDomain(): FightStat {
    return FightStat(
        round = round,
        fighterId = fighterId,
        knockdowns = knockdowns ?: 0,
        sigStrikesLanded = sigStrikesLanded ?: 0,
        sigStrikesAttempted = sigStrikesAttempted ?: 0,
        totalStrikesLanded = totalStrikesLanded ?: 0,
        totalStrikesAttempted = totalStrikesAttempted ?: 0,
        takedownsLanded = takedownsLanded ?: 0,
        takedownsAttempted = takedownsAttempted ?: 0,
        submissionAttempts = submissionAttempts ?: 0,
        reversals = reversals ?: 0,
        controlTimeSeconds = controlTimeSeconds ?: 0,
        headLanded = headLanded ?: 0,
        headAttempted = headAttempted ?: 0,
        bodyLanded = bodyLanded ?: 0,
        bodyAttempted = bodyAttempted ?: 0,
        legLanded = legLanded ?: 0,
        legAttempted = legAttempted ?: 0,
        distanceLanded = distanceLanded ?: 0,
        distanceAttempted = distanceAttempted ?: 0,
        clinchLanded = clinchLanded ?: 0,
        clinchAttempted = clinchAttempted ?: 0,
        groundLanded = groundLanded ?: 0,
        groundAttempted = groundAttempted ?: 0
    )
}

