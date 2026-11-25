package com.berkekucuk.mmaapp.presentation.screens.profile

import com.berkekucuk.mmaapp.domain.model.Participant
import com.berkekucuk.mmaapp.presentation.components.FighterImage
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.berkekucuk.mmaapp.presentation.components.appClickable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berkekucuk.mmaapp.core.presentation.LocalOddsFormat
import com.berkekucuk.mmaapp.core.presentation.formatOdds
import com.berkekucuk.mmaapp.domain.model.Prediction
import com.berkekucuk.mmaapp.presentation.components.pickShortText

@Composable
fun PredictionCard(
    prediction: Prediction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fight = prediction.fight ?: return

    val redCorner = fight.redCorner
    val blueCorner = fight.blueCorner

    val selectedParticipantIndex = when (prediction.predictedWinnerId) {
        redCorner?.fighter?.fighterId -> "1"
        blueCorner?.fighter?.fighterId -> "2"
        else -> "?"
    }

    val oddsFormat = LocalOddsFormat.current
    val formattedOdds = remember(prediction.lockedOdds, oddsFormat) {
        (prediction.lockedOdds ?: 0).formatOdds(oddsFormat)
    }
    val circleBgColor = when (prediction.isCorrect) {
        true -> AppTheme.colors.winnerFrame
        false -> AppTheme.colors.loseColor
        null -> AppTheme.colors.upcomingColor
    }
    val circleTextColor = when (prediction.isCorrect) {
        true -> AppTheme.colors.winColor2
        false -> AppTheme.colors.loseColor2
        null -> AppTheme.colors.upcomingColor2
    }
    val oddsTextColor = when (prediction.isCorrect) {
        true -> AppTheme.colors.winnerFrame
        false -> AppTheme.colors.loseColor
        null -> AppTheme.colors.upcomingColor2
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .appClickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.fightItemBackground
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PredictionFighterImage(participant = redCorner, alignment = Alignment.Start)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = redCorner?.fighter?.name ?: "",
                    color = AppTheme.colors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "VS",
                    color = AppTheme.colors.textSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Text(
                    text = blueCorner?.fighter?.name ?: "",
                    color = AppTheme.colors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                PredictionFighterImage(participant = blueCorner, alignment = Alignment.End)
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.8.dp,
                color = AppTheme.colors.dividerColor.copy(alpha = 0.5f)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = pickShortText(prediction.predictedMethod, prediction.predictedRound),
                    color = AppTheme.colors.textSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.CenterStart)
                )

                if (prediction.isCorrect == true) {
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "+${prediction.pointsEarned}",
                            color = AppTheme.colors.winnerFrame,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = AppTheme.colors.winnerFrame,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(circleBgColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = selectedParticipantIndex,
                            color = circleTextColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = formattedOdds,
                        color = oddsTextColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun PredictionFighterImage(
    participant: Participant?,
    alignment: Alignment.Horizontal,
) {
    FighterImage(
        imageUrl = participant?.fighter?.imageUrl ?: "",
        name = participant?.fighter?.name ?: "",
        countryCode = participant?.fighter?.countryCode ?: "",
        alignment = alignment,
        result = participant?.result?.name,
        imageSize = 34.dp,
        flagWidth = 14.dp,
        flagHeight = 9.dp,
    )
}