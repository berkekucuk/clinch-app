package com.berkekucuk.mmaapp.data.mapper

import com.berkekucuk.mmaapp.data.local.entity.PredictionEntity
import com.berkekucuk.mmaapp.data.local.relation.PredictionWithFightRelation
import com.berkekucuk.mmaapp.data.remote.dto.PredictionDto
import com.berkekucuk.mmaapp.domain.model.OddsMethod
import com.berkekucuk.mmaapp.domain.model.Prediction
import kotlin.time.Instant

fun PredictionDto.toEntity(): PredictionEntity =
    PredictionEntity(
        id = id,
        fightId = fightId,
        userId = userId,
        predictedWinnerId = predictedWinnerId ?: "",
        pointsEarned = pointsEarned ?: 0,
        isCorrect = isCorrect,
        lockedOdds = lockedOdds,
        predictedMethod = predictedMethod,
        predictedRound = predictedRound,
        createdAt = createdAt ?: Instant.fromEpochMilliseconds(0)
    )

fun PredictionWithFightRelation.toDomain(): Prediction =
    Prediction(
        predictionId = prediction.id,
        fightId = prediction.fightId,
        userId = prediction.userId,
        predictedWinnerId = prediction.predictedWinnerId,
        pointsEarned = prediction.pointsEarned,
        isCorrect = prediction.isCorrect,
        lockedOdds = prediction.lockedOdds,
        predictedMethod = prediction.predictedMethod?.let { OddsMethod.fromDbValue(it) },
        predictedRound = prediction.predictedRound,
        createdAt = prediction.createdAt,
        fight = fight?.toDomain()
    )
