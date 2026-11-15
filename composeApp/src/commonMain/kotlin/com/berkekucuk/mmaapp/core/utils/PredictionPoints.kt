package com.berkekucuk.mmaapp.core.utils

import kotlin.math.abs
import kotlin.math.roundToInt

fun predictionPoints(americanOdds: Int): Int {
    return when {
        americanOdds > 0 -> (10 + americanOdds / 10.0).roundToInt()
        americanOdds < 0 -> (10 + 1000.0 / abs(americanOdds)).roundToInt()
        else -> 20
    }
}
