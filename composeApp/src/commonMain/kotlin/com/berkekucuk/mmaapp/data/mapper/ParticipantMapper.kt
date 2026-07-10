package com.berkekucuk.mmaapp.data.mapper

import com.berkekucuk.mmaapp.data.remote.dto.ParticipantDto
import com.berkekucuk.mmaapp.domain.enums.Result
import com.berkekucuk.mmaapp.domain.model.Fighter
import com.berkekucuk.mmaapp.domain.model.Measurement
import com.berkekucuk.mmaapp.domain.model.Participant
import com.berkekucuk.mmaapp.domain.model.Record

fun ParticipantDto.toDomain(): Participant {
    return Participant(
        oddsValue = oddsValue,
        oddsLabel = oddsLabel ?: "",
        result = Result.fromString(result),
        recordAfterFight = recordAfterFight?.toDomain(),
        isRedCorner = isRedCorner ?: false,
        fighter = fighter?.toDomain() ?: createUnknownFighter()
    )
}

private fun createUnknownFighter(): Fighter {
    return Fighter(
        fighterId = "",
        name = "Unknown Fighter",
        nickname = "",
        record = Record.EMPTY,
        dateOfBirth = "",
        height = Measurement.EMPTY,
        reach = Measurement.EMPTY,
        weightClassId = "",
        born = "",
        fightingOutOf = "",
        countryCode = "",
        imageUrl = "",
        winRate = 0f,
        koTkoRate = 0f,
        submissionRate = 0f
    )
}
