package com.berkekucuk.mmaapp.presentation.screens.fight_detail

import com.berkekucuk.mmaapp.domain.model.Participant

const val RADAR_AXIS_COUNT = 6
private const val MIN_HEIGHT = 155f
private const val MAX_HEIGHT = 200f
private const val MIN_REACH = 155f
private const val MAX_REACH = 210f
private const val MIN_ODDS = -600f
private const val MAX_ODDS = 600f

fun buildRadarValues(participant: Participant?): List<Float> {
    val fighter = participant?.fighter
    return listOf(
        normalizeValue(fighter?.height?.metric?.toFloat(), MIN_HEIGHT, MAX_HEIGHT),
        normalizeValue(fighter?.reach?.metric?.toFloat(), MIN_REACH, MAX_REACH),
        normalizeOdds(participant?.oddsValue),
        fighter?.winRate ?: 0f,
        fighter?.koTkoRate ?: 0f,
        fighter?.submissionRate ?: 0f,
    )
}

private fun normalizeValue(value: Float?, min: Float, max: Float): Float {
    if (value == null) return 0f
    return ((value - min) / (max - min)).coerceIn(0f, 1f)
}

private fun normalizeOdds(odds: Int?): Float {
    if (odds == null) return 0.5f
    val normalized = ((-odds.toFloat()) - MIN_ODDS) / (MAX_ODDS - MIN_ODDS)
    return normalized.coerceIn(0f, 1f)
}