package com.berkekucuk.mmaapp.presentation.screens.fight_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.berkekucuk.mmaapp.core.presentation.AppTheme
import com.berkekucuk.mmaapp.core.presentation.LocalMeasurementUnit
import com.berkekucuk.mmaapp.core.presentation.LocalOddsFormat
import com.berkekucuk.mmaapp.core.presentation.MeasurementUnit
import com.berkekucuk.mmaapp.core.presentation.formatOdds
import com.berkekucuk.mmaapp.core.utils.calculateAgeAtDate
import com.berkekucuk.mmaapp.domain.model.FightOdds
import com.berkekucuk.mmaapp.domain.model.OddsMethod
import com.berkekucuk.mmaapp.domain.model.Participant
import com.berkekucuk.mmaapp.domain.model.moneyline
import com.berkekucuk.mmaapp.domain.model.oddsFor
import com.berkekucuk.mmaapp.presentation.components.appClickable
import com.berkekucuk.mmaapp.presentation.components.label
import kotlin.time.Instant

@Composable
fun FightDetailContainer(
    redCorner: Participant?,
    blueCorner: Participant?,
    eventDate: Instant?,
    odds: List<FightOdds>,
    isCompleted: Boolean,
    modifier: Modifier = Modifier,
) {
    val redFighter = redCorner?.fighter
    val blueFighter = blueCorner?.fighter

    val redFighterAge = remember(redFighter?.dateOfBirth, eventDate) {
        calculateAgeAtDate(redFighter?.dateOfBirth, eventDate)
    }
    val blueFighterAge = remember(blueFighter?.dateOfBirth, eventDate) {
        calculateAgeAtDate(blueFighter?.dateOfBirth, eventDate)
    }

    val measurementUnit = LocalMeasurementUnit.current
    val oddsFormat = LocalOddsFormat.current
    val showRecord = redCorner?.recordAfterFight != null || blueCorner?.recordAfterFight != null
    val detailedOddsRows = detailedOddsRows(
        odds = odds,
        redId = redFighter?.fighterId,
        blueId = blueFighter?.fighterId,
    ).map { (left, label, right) -> Triple(left?.formatOdds(oddsFormat), label, right?.formatOdds(oddsFormat)) }
    val canExpandOdds = detailedOddsRows.isNotEmpty() || !isCompleted
    var showDetailedOdds by rememberSaveable { mutableStateOf(false) }

    val rows = buildList {
        add(Triple(redFighterAge, AppTheme.strings.fightDetailLabelAge, blueFighterAge))
        add(Triple(
            when (measurementUnit) {
                MeasurementUnit.METRIC -> redFighter?.height?.metric?.let { AppTheme.strings.heightCm(it.toString()) }
                MeasurementUnit.IMPERIAL -> redFighter?.height?.imperial?.ifBlank { null }
            },
            AppTheme.strings.fightDetailLabelHeight,
            when (measurementUnit) {
                MeasurementUnit.METRIC -> blueFighter?.height?.metric?.let { AppTheme.strings.heightCm(it.toString()) }
                MeasurementUnit.IMPERIAL -> blueFighter?.height?.imperial?.ifBlank { null }
            },
        ))
        add(Triple(
            when (measurementUnit) {
                MeasurementUnit.METRIC -> redFighter?.reach?.metric?.let { AppTheme.strings.heightCm(it.toString()) }
                MeasurementUnit.IMPERIAL -> redFighter?.reach?.imperial?.ifBlank { null }
            },
            AppTheme.strings.fightDetailLabelReach,
            when (measurementUnit) {
                MeasurementUnit.METRIC -> blueFighter?.reach?.metric?.let { AppTheme.strings.heightCm(it.toString()) }
                MeasurementUnit.IMPERIAL -> blueFighter?.reach?.imperial?.ifBlank { null }
            },
        ))
        if (showRecord) {
            add(Triple(redCorner?.recordAfterFight?.toString(), AppTheme.strings.fightDetailLabelRecord, blueCorner?.recordAfterFight?.toString()))
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.fightItemBackground)
    ) {
        Column {
            rows.forEach { (leftValue, label, rightValue) ->
                StatRow(
                    leftValue = leftValue ?: "—",
                    label = label,
                    rightValue = rightValue ?: "—",
                )
                RowDivider()
            }

            StatRow(
                leftValue = odds.moneyline(redFighter?.fighterId)?.formatOdds(oddsFormat) ?: "—",
                label = AppTheme.strings.fightDetailLabelOdds,
                rightValue = odds.moneyline(blueFighter?.fighterId)?.formatOdds(oddsFormat) ?: "—",
                isExpanded = if (canExpandOdds) showDetailedOdds else null,
                onClick = if (canExpandOdds) ({ showDetailedOdds = !showDetailedOdds }) else null,
            )
            if (canExpandOdds) {
                AnimatedVisibility(
                    visible = showDetailedOdds,
                    enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut(),
                ) {
                    Column {
                        if (detailedOddsRows.isEmpty()) {
                            RowDivider()
                            Text(
                                text = AppTheme.strings.fightOddsNotAnnounced,
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppTheme.colors.textSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                            )
                        }
                        detailedOddsRows.forEach { (leftValue, label, rightValue) ->
                            RowDivider()
                            StatRow(
                                leftValue = leftValue ?: "—",
                                label = label,
                                rightValue = rightValue ?: "—",
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        color = AppTheme.colors.dividerColor,
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 12.dp),
    )
}

@Composable
private fun StatRow(
    leftValue: String,
    label: String,
    rightValue: String,
    isExpanded: Boolean? = null,
    onClick: (() -> Unit)? = null,
) {
    val rotation by animateFloatAsState(targetValue = if (isExpanded == true) 180f else 0f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.appClickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = leftValue,
            style = MaterialTheme.typography.bodyMedium,
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f),
        )

        Row(
            modifier = Modifier.weight(1.5f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
            )
            if (isExpanded != null) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = AppTheme.colors.textSecondary,
                    modifier = Modifier
                        .size(18.dp)
                        .rotate(rotation)
                )
            }
        }

        Text(
            text = rightValue,
            style = MaterialTheme.typography.bodyMedium,
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun detailedOddsRows(
    odds: List<FightOdds>,
    redId: String?,
    blueId: String?,
): List<Triple<Int?, String, Int?>> {
    val rows = mutableListOf<Triple<Int?, String, Int?>>()
    OddsMethod.METHODS.forEach { method ->
        val red = odds.oddsFor(redId, method)
        val blue = odds.oddsFor(blueId, method)
        if (red != null || blue != null) rows += Triple(red, method.label(), blue)
    }
    val maxRound = odds.filter { it.method in OddsMethod.FINISH_METHODS }.maxOfOrNull { it.round } ?: 0
    for (round in 1..maxRound) {
        OddsMethod.FINISH_METHODS.forEach { method ->
            val red = odds.oddsFor(redId, method, round)
            val blue = odds.oddsFor(blueId, method, round)
            if (red != null || blue != null) {
                rows += Triple(red, AppTheme.strings.fightOddsRoundMethod(round, method.label()), blue)
            }
        }
    }
    return rows
}