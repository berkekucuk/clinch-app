package com.berkekucuk.mmaapp.presentation.components

import androidx.compose.runtime.Composable
import com.berkekucuk.mmaapp.core.presentation.AppTheme
import com.berkekucuk.mmaapp.domain.model.OddsMethod

@Composable
fun OddsMethod.label(): String = when (this) {
    OddsMethod.ANY -> AppTheme.strings.predictionAnyOption
    OddsMethod.KO_TKO -> AppTheme.strings.fightOddsKoTko
    OddsMethod.SUBMISSION -> AppTheme.strings.fightOddsSubmission
    OddsMethod.DECISION -> AppTheme.strings.fightOddsDecision
}

@Composable
fun pickDetailText(method: OddsMethod?, round: Int?): String? {
    if (method == null) return null
    return listOfNotNull(method.label(), round?.let { AppTheme.strings.fightOddsRound(it) }).joinToString(" · ")
}

@Composable
fun pickShortText(method: OddsMethod?, round: Int?): String {
    if (method == null) return AppTheme.strings.predictionMatchResult
    return listOfNotNull(method.label(), round?.let { "R$it" }).joinToString(" · ")
}