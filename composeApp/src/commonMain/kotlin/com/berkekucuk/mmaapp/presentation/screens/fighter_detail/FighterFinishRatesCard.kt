package com.berkekucuk.mmaapp.presentation.screens.fighter_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
import com.berkekucuk.mmaapp.presentation.screens.fight_detail.formatRadarPercent

@Composable
fun FighterFinishRatesCard(
    koTkoRate: Float,
    submissionRate: Float,
) {
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.fightItemBackground)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FighterStatItem(
            value = formatRadarPercent(koTkoRate),
            label = strings.fighterDetailLabelKoTkoRate,
            valueColor = colors.textPrimary,
            valueStyle = MaterialTheme.typography.titleMedium,
            labelStyle = MaterialTheme.typography.labelSmall,
        )
        FighterStatItem(
            value = formatRadarPercent(submissionRate),
            label = strings.fighterDetailLabelSubRate,
            valueColor = colors.textPrimary,
            valueStyle = MaterialTheme.typography.titleMedium,
            labelStyle = MaterialTheme.typography.labelSmall,
        )
    }
}
