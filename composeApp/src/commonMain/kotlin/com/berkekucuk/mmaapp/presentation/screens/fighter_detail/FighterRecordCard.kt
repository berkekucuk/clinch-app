package com.berkekucuk.mmaapp.presentation.screens.fighter_detail

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.berkekucuk.mmaapp.domain.model.Record

@Composable
fun FighterRecordCard(
    record: Record,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppTheme.colors.fightItemBackground)
            .padding(vertical = 10.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FighterStatItem(
            value = record.wins.toString(),
            label = AppTheme.strings.fighterDetailRecordWins,
            valueColor = AppTheme.colors.winColor,
        )
        FighterStatItem(
            value = record.losses.toString(),
            label = AppTheme.strings.fighterDetailRecordLosses,
            valueColor = AppTheme.colors.loseColor,
        )
        FighterStatItem(
            value = record.draws.toString(),
            label = AppTheme.strings.fighterDetailRecordDraws,
            valueColor = AppTheme.colors.textSecondary,
        )
    }
}
