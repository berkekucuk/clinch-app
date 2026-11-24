package com.berkekucuk.mmaapp.core.presentation

import androidx.compose.runtime.staticCompositionLocalOf
import kotlin.math.abs
import kotlin.math.round

enum class OddsFormat { DECIMAL, AMERICAN }

val LocalOddsFormat = staticCompositionLocalOf { OddsFormat.DECIMAL }

fun Int.toDecimalOdds(): String {
    val decimal = if (this >= 0) {
        (this / 100.0) + 1.0
    } else {
        (100.0 / abs(this)) + 1.0
    }
    val rounded = round(decimal * 100).toInt()
    val intPart = rounded / 100
    val fracPart = rounded % 100
    return "$intPart.${fracPart.toString().padStart(2, '0')}"
}

fun Int.toAmericanOdds(): String {
    val sign = if (this >= 0) "+" else ""
    return "$sign$this"
}

fun Int.formatOdds(format: OddsFormat): String =
    if (format == OddsFormat.DECIMAL) toDecimalOdds() else toAmericanOdds()
