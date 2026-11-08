package com.berkekucuk.mmaapp.presentation.screens.fight_detail

import com.berkekucuk.mmaapp.core.presentation.strings.AppStrings
import com.berkekucuk.mmaapp.domain.model.Fighter
import kotlin.math.roundToInt

internal enum class RadarAxis(
    val isPercent: Boolean,
    val percentileAnchors: List<Float>,
    val value: (Fighter) -> Float,
    val label: (AppStrings) -> String,
) {
    SLPM(
        isPercent = false,
        percentileAnchors = listOf(0f, 2.18f, 2.73f, 3.18f, 3.55f, 3.9f, 4.28f, 4.65f, 5.19f, 6.04f, 10.83f),
        value = { it.slpm },
        label = { it.radarLabelSlpm },
    ),
    STR_ACC(
        isPercent = true,
        percentileAnchors = listOf(0f, 0.38f, 0.41f, 0.44f, 0.46f, 0.48f, 0.5f, 0.51f, 0.54f, 0.58f, 0.7f),
        value = { it.strAcc },
        label = { it.radarLabelStrAcc },
    ),
    TD_AVG(
        isPercent = false,
        percentileAnchors = listOf(0f, 0f, 0.15f, 0.5f, 0.77f, 1.06f, 1.45f, 1.92f, 2.5f, 3.59f, 8.22f),
        value = { it.tdAvg },
        label = { it.radarLabelTdAvg },
    ),
    TD_ACC(
        isPercent = true,
        percentileAnchors = listOf(0f, 0f, 0.09f, 0.23f, 0.3f, 0.34f, 0.39f, 0.45f, 0.5f, 0.6f, 1f),
        value = { it.tdAcc },
        label = { it.radarLabelTdAcc },
    ),
    SUB_AVG(
        isPercent = false,
        percentileAnchors = listOf(0f, 0f, 0f, 0f, 0.07f, 0.22f, 0.37f, 0.55f, 0.81f, 1.28f, 3.54f),
        value = { it.subAvg },
        label = { it.radarLabelSubAvg },
    ),
    TD_DEF(
        isPercent = true,
        percentileAnchors = listOf(0f, 0f, 0.4f, 0.5f, 0.57f, 0.64f, 0.68f, 0.73f, 0.8f, 0.9f, 1f),
        value = { it.tdDef },
        label = { it.radarLabelTdDef },
    ),
    STR_DEF(
        isPercent = true,
        percentileAnchors = listOf(0f, 0.41f, 0.45f, 0.49f, 0.51f, 0.53f, 0.55f, 0.56f, 0.59f, 0.62f, 0.72f),
        value = { it.strDef },
        label = { it.radarLabelStrDef },
    ),
}

val RADAR_AXIS_COUNT = RadarAxis.entries.size

internal fun buildRadarLabels(strings: AppStrings): List<String> {
    return RadarAxis.entries.map { it.label(strings) }
}

internal fun buildRadarValues(fighter: Fighter?): List<Float> {
    return RadarAxis.entries.map { axis ->
        if (fighter == null) 0f else scaleRadarValue(axis, axis.value(fighter))
    }
}

internal fun buildRadarDisplayValues(fighter: Fighter): List<String> {
    return RadarAxis.entries.map { axis ->
        val value = axis.value(fighter)
        if (axis.isPercent) formatRadarPercent(value) else formatRadarRate(value)
    }
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

private fun scaleRadarValue(axis: RadarAxis, rawValue: Float): Float {
    val value = if (axis.isPercent && rawValue > 1.0f) rawValue / 100f else rawValue
    val anchors = axis.percentileAnchors
    val step = 1f / (anchors.size - 1)

    for (i in 0 until anchors.lastIndex) {
        val low = anchors[i]
        val high = anchors[i + 1]
        if (value <= high) {
            if (high <= low) return i * step
            return (i * step + step * (value - low) / (high - low)).coerceIn(0f, 1f)
        }
    }
    return 1f
}
