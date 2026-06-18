package com.berkekucuk.mmaapp.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berkekucuk.mmaapp.core.presentation.LocalOddsFormat
import com.berkekucuk.mmaapp.core.presentation.OddsFormat
import com.berkekucuk.mmaapp.core.presentation.toAmericanOdds
import com.berkekucuk.mmaapp.core.presentation.toDecimalOdds
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
import com.berkekucuk.mmaapp.domain.model.Prediction
import com.berkekucuk.mmaapp.presentation.components.FightItem

@Composable
fun PredictionCard(
    prediction: Prediction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val strings = LocalAppStrings.current
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
        val oddsValue = prediction.lockedOdds ?: 0
        if (oddsFormat == OddsFormat.AMERICAN) {
            oddsValue.toAmericanOdds()
        } else {
            oddsValue.toDecimalOdds()
        }
    }
    val circleBgColor = when (prediction.isCorrect) {
        true -> colors.winnerFrame
        false -> colors.loseColor
        null -> colors.upcomingColor
    }
    val circleTextColor = when (prediction.isCorrect) {
        true -> colors.winColor2
        false -> colors.loseColor2
        null -> colors.upcomingColor2
    }
    val oddsTextColor = when (prediction.isCorrect) {
        true -> colors.winnerFrame
        false -> colors.loseColor
        null -> colors.upcomingColor2
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.fightItemBackground
        )
    ) {
        Column {
            FightItem(
                fight = fight,
                modifier = Modifier.height(108.dp),
                backgroundColor = Color.Transparent
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.8.dp,
                color = colors.dividerColor.copy(alpha = 0.5f)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = strings.predictionQuestionTitle,
                    color = colors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.CenterStart)
                )

                if (prediction.isCorrect != null) {
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val sign = if (prediction.pointsEarned > 0) "+" else ""
                        Text(
                            text = "$sign${prediction.pointsEarned}",
                            color = if (prediction.isCorrect) colors.winnerFrame else colors.ufcRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = if (prediction.isCorrect) colors.winnerFrame else colors.ufcRed,
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
