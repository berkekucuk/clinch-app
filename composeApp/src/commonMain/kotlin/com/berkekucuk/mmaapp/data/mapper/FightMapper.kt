package com.berkekucuk.mmaapp.data.mapper

import com.berkekucuk.mmaapp.data.local.entity.FightEntity
import com.berkekucuk.mmaapp.data.local.relation.FightWithStatsRelation
import com.berkekucuk.mmaapp.data.remote.dto.FightDto
import com.berkekucuk.mmaapp.domain.model.Fight
import com.berkekucuk.mmaapp.domain.model.FightStat

fun FightDto.toEntity(): FightEntity {
    return FightEntity(
        fightId = fightId,
        eventId = eventId,
        eventName = eventName,
        eventDate = eventDate,
        methodType = methodType,
        methodDetail = methodDetail,
        roundSummary = roundSummary,
        boutType = boutType ?: "",
        weightClassLbs = weightClassLbs,
        weightClassId = weightClassId ?: "",
        roundsFormat = roundsFormat ?: "",
        fightOrder = fightOrder ?: 0,
        titleType = titleType,
        referee = referee,
        bonuses = bonuses,
        participants = participants ?: emptyList()
    )
}

fun FightEntity.toDomain(stats: List<FightStat> = emptyList()): Fight {
    return Fight(
        fightId = fightId,
        eventId = eventId,
        eventName = eventName,
        eventDate = eventDate,
        methodType = methodType,
        methodDetail = methodDetail,
        roundSummary = roundSummary,
        boutType = boutType,
        weightClassLbs = weightClassLbs,
        weightClassId = weightClassId,
        roundsFormat = roundsFormat,
        fightOrder = fightOrder,
        titleType = titleType,
        referee = referee,
        bonuses = bonuses ?: emptyList(),
        participants = participants.map { it.toDomain() },
        stats = stats
    )
}

fun FightWithStatsRelation.toDomain(): Fight {
    return fight.toDomain(stats = stats.map { it.toDomain() })
}

fun FightDto.toDomain(): Fight {
    return Fight(
        fightId = fightId,
        eventId = eventId,
        eventName = eventName,
        eventDate = eventDate,
        methodType = methodType,
        methodDetail = methodDetail,
        roundSummary = roundSummary,
        boutType = boutType ?: "",
        weightClassLbs = weightClassLbs,
        weightClassId = weightClassId ?: "",
        roundsFormat = roundsFormat ?: "",
        fightOrder = fightOrder ?: 0,
        titleType = titleType,
        referee = referee,
        bonuses = bonuses ?: emptyList(),
        participants = participants?.map { it.toDomain() } ?: emptyList(),
        stats = stats?.map { it.toDomain() } ?: emptyList()
    )
}