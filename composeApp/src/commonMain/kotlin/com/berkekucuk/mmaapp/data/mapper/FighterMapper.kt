package com.berkekucuk.mmaapp.data.mapper

import com.berkekucuk.mmaapp.data.local.entity.FighterEntity
import com.berkekucuk.mmaapp.data.remote.dto.FighterDto
import com.berkekucuk.mmaapp.domain.model.Fighter
import com.berkekucuk.mmaapp.domain.model.Measurement
import com.berkekucuk.mmaapp.domain.model.Record
import com.berkekucuk.mmaapp.data.local.relation.FighterWithFightsRelation

fun FighterDto.toEntity(): FighterEntity {
    return FighterEntity(
        fighterId = fighterId,
        name = name,
        nickname = nickname,
        imageUrl = imageUrl,
        record = record,
        height = height,
        reach = reach,
        weightClassId = weightClassId,
        dateOfBirth = dateOfBirth,
        born = born,
        fightingOutOf = fightingOutOf,
        countryCode = countryCode,
        winRate = winRate,
        koTkoRate = koTkoRate,
        submissionRate = submissionRate
    )
}

fun FighterEntity.toDomain(): Fighter{
    return Fighter(
        fighterId = fighterId,
        name = name ?: "",
        nickname = nickname ?: "",
        imageUrl = imageUrl ?: "",
        record = record?.toDomain() ?: Record.EMPTY,
        height = height?.toDomain() ?: Measurement.EMPTY,
        reach = reach?.toDomain() ?: Measurement.EMPTY,
        weightClassId = weightClassId ?: "",
        dateOfBirth = dateOfBirth ?: "",
        born = born ?: "",
        fightingOutOf = fightingOutOf ?: "",
        countryCode = countryCode ?: "",
        winRate = winRate ?: 0f,
        koTkoRate = koTkoRate ?: 0f,
        submissionRate = submissionRate ?: 0f
    )
}

fun Fighter.toEntity(): FighterEntity {
    return FighterEntity(
        fighterId = fighterId,
        name = name,
        nickname = nickname,
        imageUrl = imageUrl,
        record = record.toDto(),
        height = height.toDto(),
        reach = reach.toDto(),
        weightClassId = weightClassId,
        dateOfBirth = dateOfBirth,
        born = born,
        fightingOutOf = fightingOutOf,
        countryCode = countryCode,
        winRate = winRate,
        koTkoRate = koTkoRate,
        submissionRate = submissionRate
    )
}

fun FighterWithFightsRelation.toDomain(): Fighter {
    return Fighter(
        fighterId = fighter.fighterId,
        name = fighter.name ?: "",
        nickname = fighter.nickname,
        imageUrl = fighter.imageUrl ?: "",
        record = fighter.record?.toDomain() ?: Record.EMPTY,
        height = fighter.height?.toDomain() ?: Measurement.EMPTY,
        reach = fighter.reach?.toDomain() ?: Measurement.EMPTY,
        weightClassId = fighter.weightClassId ?: "",
        dateOfBirth = fighter.dateOfBirth ?: "",
        born = fighter.born,
        fightingOutOf = fighter.fightingOutOf,
        countryCode = fighter.countryCode ?: "",
        winRate = fighter.winRate ?: 0f,
        koTkoRate = fighter.koTkoRate ?: 0f,
        submissionRate = fighter.submissionRate ?: 0f,
        fights = fights.map { it.toDomain() }
            .filterNot { it.isCancelledOrFizzled }
            .sortedByDescending { it.eventDate },
    )
}

fun FighterDto.toDomain(): Fighter {
    return Fighter(
        fighterId = fighterId,
        name = name ?: "",
        nickname = nickname ?: "",
        imageUrl = imageUrl ?: "",
        record = record?.toDomain() ?: Record.EMPTY,
        height = height?.toDomain() ?: Measurement.EMPTY,
        reach = reach?.toDomain() ?: Measurement.EMPTY,
        weightClassId = weightClassId ?: "",
        dateOfBirth = dateOfBirth ?: "",
        born = born ?: "",
        fightingOutOf = fightingOutOf ?: "",
        countryCode = countryCode ?: "",
        winRate = winRate ?: 0f,
        koTkoRate = koTkoRate ?: 0f,
        submissionRate = submissionRate ?: 0f
    )
}