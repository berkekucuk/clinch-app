package com.berkekucuk.mmaapp.presentation.screens.fight_detail

import com.berkekucuk.mmaapp.domain.model.Fighter
import kotlin.math.roundToInt

const val RADAR_AXIS_COUNT = 9

fun buildRadarValues(fighter: Fighter?): List<Float> {
    return listOf(
        normalizeValue(fighter?.slpm, 8f),
        normalizePercent(fighter?.strAcc),
        normalizePercent(fighter?.strDef),
        normalizeValue(fighter?.tdAvg, 4.5f),
        normalizePercent(fighter?.tdAcc),
        normalizePercent(fighter?.tdDef),
        normalizePercent(fighter?.submissionRate),
        normalizePercent(fighter?.koTkoRate),
        normalizePercent(fighter?.winRate)
    )
}

internal fun formatRadarRate(value: Float?): String {
    if (value == null) return "—"
    if (value == 0f) return "0"
    val rounded = (value * 100).roundToInt() / 100f
    return if (rounded % 1f == 0f) rounded.toInt().toString() else rounded.toString()
}

internal fun formatRadarPercent(value: Float?): String {
    if (value == null) return "—"
    val fraction = if (value <= 1.0f) value else value / 100f
    val percentInt = (fraction.coerceIn(0f, 1f) * 100).roundToInt()
    return "$percentInt%"
}

private fun normalizePercent(value: Float?): Float {
    if (value == null) return 0f
    val fraction = if (value <= 1.0f) value else value / 100f
    return fraction.coerceIn(0f, 1f)
}

private fun normalizeValue(value: Float?, max: Float): Float {
    if (value == null || max <= 0f) return 0f
    return (value / max).coerceIn(0f, 1f)
}