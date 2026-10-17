package com.berkekucuk.mmaapp.presentation.screens.fighter_detail

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun FighterStatItem(
    value: String,
    label: String,
    valueColor: Color,
    valueStyle: TextStyle = MaterialTheme.typography.headlineSmall,
    labelStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = value,
            style = valueStyle,
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
        )
        Text(
            text = label,
            style = labelStyle,
            fontWeight = FontWeight.SemiBold,
            color = AppTheme.colors.textSecondary,
        )
    }
}
