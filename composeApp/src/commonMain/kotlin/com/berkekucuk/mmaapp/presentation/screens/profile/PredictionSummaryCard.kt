package com.berkekucuk.mmaapp.presentation.screens.profile

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berkekucuk.mmaapp.domain.model.User

@Composable
fun PredictionSummaryCard(
    user: User,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.fightItemBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SummaryStat(
                value = "${user.correctCount}/${user.decidedCount}",
                label = AppTheme.strings.profileCorrectPredictions,
                modifier = Modifier.weight(1f)
            )
            VerticalDivider(
                modifier = Modifier
                    .height(28.dp)
                    .width(0.8.dp),
                color = AppTheme.colors.dividerColor.copy(alpha = 0.5f)
            )
            SummaryStat(
                value = user.accuracyPercent?.let { AppTheme.strings.percentValue(it) } ?: "—",
                label = AppTheme.strings.profileAccuracy,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryStat(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            color = AppTheme.colors.textPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            color = AppTheme.colors.textSecondary,
            fontSize = 12.sp
        )
    }
}
