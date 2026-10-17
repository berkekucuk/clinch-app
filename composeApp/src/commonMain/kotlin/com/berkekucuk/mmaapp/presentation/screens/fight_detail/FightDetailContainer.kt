package com.berkekucuk.mmaapp.presentation.screens.fight_detail

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.berkekucuk.mmaapp.core.presentation.LocalMeasurementUnit
import com.berkekucuk.mmaapp.core.presentation.LocalOddsFormat
import com.berkekucuk.mmaapp.core.presentation.MeasurementUnit
import com.berkekucuk.mmaapp.core.presentation.OddsFormat
import com.berkekucuk.mmaapp.core.presentation.toAmericanOdds
import com.berkekucuk.mmaapp.core.presentation.toDecimalOdds
import com.berkekucuk.mmaapp.core.utils.calculateAgeAtDate
import com.berkekucuk.mmaapp.domain.model.Participant
import kotlin.time.Instant

@Composable
fun FightDetailContainer(
    redCorner: Participant?,
    blueCorner: Participant?,
    eventDate: Instant?,
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
        add(Triple(
            redCorner?.oddsValue?.let { if (oddsFormat == OddsFormat.DECIMAL) it.toDecimalOdds() else it.toAmericanOdds() },
            AppTheme.strings.fightDetailLabelOdds,
            blueCorner?.oddsValue?.let { if (oddsFormat == OddsFormat.DECIMAL) it.toDecimalOdds() else it.toAmericanOdds() },
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
            rows.forEachIndexed { index, (leftValue, label, rightValue) ->
                StatRow(
                    leftValue = leftValue ?: "—",
                    label = label,
                    rightValue = rightValue ?: "—",
                )
                if (index < rows.lastIndex) {
                    HorizontalDivider(
                        color = AppTheme.colors.dividerColor,
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun StatRow(
    leftValue: String,
    label: String,
    rightValue: String,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
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

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = AppTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1.5f),
        )

        Text(
            text = rightValue,
            style = MaterialTheme.typography.bodyMedium,
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
}