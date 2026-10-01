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
        winRate = winRate ?: 0f,
        koTkoRate = koTkoRate ?: 0f,
        submissionRate = submissionRate ?: 0f,
        slpm = slpm ?: 0f,
        strAcc = strAcc ?: 0f,
        sapm = sapm ?: 0f,
        strDef = strDef ?: 0f,
        tdAvg = tdAvg ?: 0f,
        tdAcc = tdAcc ?: 0f,
        tdDef = tdDef ?: 0f,
        subAvg = subAvg ?: 0f
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
        slpm = fighter.slpm ?: 0f,
        strAcc = fighter.strAcc ?: 0f,
        sapm = fighter.sapm ?: 0f,
        strDef = fighter.strDef ?: 0f,
        tdAvg = fighter.tdAvg ?: 0f,
        tdAcc = fighter.tdAcc ?: 0f,
        tdDef = fighter.tdDef ?: 0f,
        subAvg = fighter.subAvg ?: 0f,
        fights = fights.map { it.toDomain() }
            .sortedByDescending { it.eventDate },
    )
}

fun FighterEntity.toDomain(): Fighter {
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
        submissionRate = submissionRate ?: 0f,
        slpm = slpm ?: 0f,
        strAcc = strAcc ?: 0f,
        sapm = sapm ?: 0f,
        strDef = strDef ?: 0f,
        tdAvg = tdAvg ?: 0f,
        tdAcc = tdAcc ?: 0f,
        tdDef = tdDef ?: 0f,
        subAvg = subAvg ?: 0f
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
        submissionRate = submissionRate,
        slpm = slpm,
        strAcc = strAcc,
        sapm = sapm,
        strDef = strDef,
        tdAvg = tdAvg,
        tdAcc = tdAcc,
        tdDef = tdDef,
        subAvg = subAvg
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
        submissionRate = submissionRate ?: 0f,
        slpm = slpm ?: 0f,
        strAcc = strAcc ?: 0f,
        sapm = sapm ?: 0f,
        strDef = strDef ?: 0f,
        tdAvg = tdAvg ?: 0f,
        tdAcc = tdAcc ?: 0f,
        tdDef = tdDef ?: 0f,
        subAvg = subAvg ?: 0f,
        fights = fights?.map { it.toDomain() } ?: emptyList()
    )
}