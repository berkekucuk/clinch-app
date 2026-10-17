package com.berkekucuk.mmaapp.presentation.screens.fight_detail

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PredictionBoard(
    state: FightDetailUiState,
    onPredict: (String) -> Unit,
    onLeaderboardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fight = state.fight ?: return

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.fightItemBackground
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppTheme.strings.predictionQuestionTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = AppTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Outlined.EmojiEvents,
                    contentDescription = null,
                    tint = AppTheme.colors.winnerFrame,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onLeaderboardClick() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val redCorner = fight.redCorner?.fighter
                val blueCorner = fight.blueCorner?.fighter

                if (redCorner != null) {
                    PredictionButton(
                        fighter = redCorner,
                        isSelected = state.predictedWinnerId == redCorner.fighterId,
                        isSubmitting = state.isSubmittingPrediction,
                        isOtherSelected = state.predictedWinnerId != null && state.predictedWinnerId != redCorner.fighterId,
                        onClick = { onPredict(redCorner.fighterId) },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (blueCorner != null) {
                    PredictionButton(
                        fighter = blueCorner,
                        isSelected = state.predictedWinnerId == blueCorner.fighterId,
                        isSubmitting = state.isSubmittingPrediction,
                        isOtherSelected = state.predictedWinnerId != null && state.predictedWinnerId != blueCorner.fighterId,
                        onClick = { onPredict(blueCorner.fighterId) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
