package com.berkekucuk.mmaapp.presentation.screens.fighter_detail

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.berkekucuk.mmaapp.core.presentation.LocalMeasurementUnit
import com.berkekucuk.mmaapp.core.presentation.MeasurementUnit
import com.berkekucuk.mmaapp.domain.model.Fighter

@Composable
fun FighterInfoCard(
    fighter: Fighter,
    age: String?,
    formattedDob: String?,
    weightClassDisplay: String,
) {
    val measurementUnit = LocalMeasurementUnit.current
    val unavailable = AppTheme.strings.fighterDetailValueUnavailable
    val rows = buildList {
        add(Pair(AppTheme.strings.fighterDetailLabelWeightClass, weightClassDisplay.ifEmpty { unavailable }))
        add(Pair(AppTheme.strings.fighterDetailLabelDateOfBirth, formattedDob ?: unavailable))
        add(Pair(AppTheme.strings.fighterDetailLabelAge, age ?: unavailable))
        add(Pair(AppTheme.strings.fighterDetailLabelHeight, when (measurementUnit) {
            MeasurementUnit.METRIC -> fighter.height.metric?.let { AppTheme.strings.heightCm(it.toString()) } ?: unavailable
            MeasurementUnit.IMPERIAL -> fighter.height.imperial?.ifBlank { null } ?: unavailable
        }))
        add(Pair(AppTheme.strings.fighterDetailLabelReach, when (measurementUnit) {
            MeasurementUnit.METRIC -> fighter.reach.metric?.let { AppTheme.strings.heightCm(it.toString()) } ?: unavailable
            MeasurementUnit.IMPERIAL -> fighter.reach.imperial?.ifBlank { null } ?: unavailable
        }))
        add(Pair(AppTheme.strings.fighterDetailLabelBorn, fighter.born?.ifBlank { null } ?: unavailable))
        add(Pair(AppTheme.strings.fighterDetailLabelFightingOutOf, fighter.fightingOutOf?.ifBlank { null } ?: unavailable))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.fightItemBackground)
    ) {
        rows.forEachIndexed { index, (label, value) ->
            InfoRow(label = label, value = value)
            if (index < rows.lastIndex) {
                HorizontalDivider(
                    color = AppTheme.colors.dividerColor,
                    thickness = 0.8.dp,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = AppTheme.colors.textSecondary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.End,
        )
    }
}
