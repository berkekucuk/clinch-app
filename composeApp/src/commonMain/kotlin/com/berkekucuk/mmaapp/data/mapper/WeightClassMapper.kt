package com.berkekucuk.mmaapp.data.mapper

import com.berkekucuk.mmaapp.data.local.entity.WeightClassEntity
import com.berkekucuk.mmaapp.data.remote.dto.WeightClassDto
import com.berkekucuk.mmaapp.domain.model.RankedFighter
import com.berkekucuk.mmaapp.domain.model.WeightClass

fun WeightClassDto.toEntity(): WeightClassEntity {
    return WeightClassEntity(
        id = id,
        sortOrder = sortOrder ?: Int.MAX_VALUE,
        rankings = rankings ?: emptyList()
    )
}

fun WeightClassEntity.toDomain(): WeightClass {
    return WeightClass(
        id = id,
        sortOrder = sortOrder,
        rankings = rankings
            .sortedBy { it.rankNumber }
            .map { RankedFighter(rankNumber = it.rankNumber, rankChange = it.rankChange, fighter = it.fighter?.toDomain()) }
    )
}
