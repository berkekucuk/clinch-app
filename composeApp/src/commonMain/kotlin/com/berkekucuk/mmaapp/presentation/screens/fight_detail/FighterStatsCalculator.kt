package com.berkekucuk.mmaapp.presentation.screens.fight_detail

import com.berkekucuk.mmaapp.domain.model.Participant

const val RADAR_AXIS_COUNT = 9

fun buildRadarValues(participant: Participant?): List<Float> {
    val fighter = participant?.fighter
    return listOf(
        normalizeValue(fighter?.slpm, 0f, 8f),
        normalizePercent(fighter?.strAcc),
        normalizePercent(fighter?.strDef),
        normalizeValue(fighter?.tdAvg, 0f, 4.5f),
        normalizePercent(fighter?.tdAcc),
        normalizePercent(fighter?.tdDef),
        normalizePercent(fighter?.submissionRate),
        normalizePercent(fighter?.koTkoRate),
        normalizePercent(fighter?.winRate)
    )
}

private fun normalizePercent(value: Float?): Float {
    if (value == null) return 0f
    val fraction = if (value <= 1.0f) value else value / 100f
    return fraction.coerceIn(0f, 1f)
}

private fun normalizeValue(value: Float?, min: Float, max: Float): Float {
    if (value == null) return 0f
    return ((value - min) / (max - min)).coerceIn(0f, 1f)
}