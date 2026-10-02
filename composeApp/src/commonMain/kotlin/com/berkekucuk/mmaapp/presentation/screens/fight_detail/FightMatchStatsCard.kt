package com.berkekucuk.mmaapp.presentation.screens.fight_detail

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
import com.berkekucuk.mmaapp.domain.model.Fight
import kotlin.math.roundToInt

@Composable
fun FightMatchStatsCard(
    fight: Fight,
    modifier: Modifier = Modifier,
) {
    if (fight.stats.isEmpty()) return

    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current

    val availableRounds = remember(fight.stats) {
        fight.stats.map { it.round }.distinct().sorted()
    }
    var selectedRound by remember(fight.fightId) {
        mutableStateOf(availableRounds.firstOrNull { it == 0 } ?: availableRounds.firstOrNull() ?: 0)
    }

    val redFighterId = fight.redCorner?.fighter?.fighterId
    val blueFighterId = fight.blueCorner?.fighter?.fighterId

    val redStat = fight.stats.find { it.round == selectedRound && it.fighterId == redFighterId }
    val blueStat = fight.stats.find { it.round == selectedRound && it.fighterId == blueFighterId }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Overview Match Stats Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.fightItemBackground)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Text(
                    text = strings.matchStatsTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // Round Selector Chips
                if (availableRounds.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                    ) {
                        availableRounds.forEach { roundNum ->
                            val isSelected = roundNum == selectedRound
                            val chipBg by animateColorAsState(
                                if (isSelected) colors.winnerFrame.copy(alpha = 0.15f) else Color.Transparent
                            )
                            val chipBorderColor by animateColorAsState(
                                if (isSelected) colors.winnerFrame else colors.dividerColor
                            )
                            val textColor by animateColorAsState(
                                if (isSelected) colors.textPrimary else colors.textSecondary
                            )

                            val label = if (roundNum == 0) strings.roundTotal else strings.roundPrefix(roundNum)

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(chipBg)
                                    .border(1.dp, chipBorderColor, RoundedCornerShape(16.dp))
                                    .clickable { selectedRound = roundNum }
                                    .padding(horizontal = 14.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = textColor
                                )
                            }
                        }
                    }
                }

                // Overview Stats
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Knockdowns
                    val redKd = redStat?.knockdowns ?: 0
                    val blueKd = blueStat?.knockdowns ?: 0
                    MatchStatRow(
                        leftValue = redKd.toString(),
                        leftNum = redKd.toFloat(),
                        label = strings.matchStatsKnockdowns,
                        rightValue = blueKd.toString(),
                        rightNum = blueKd.toFloat()
                    )

                    // Sig Strikes (Landed / Attempted + %)
                    val redSigLanded = redStat?.sigStrikesLanded ?: 0
                    val redSigAttempted = redStat?.sigStrikesAttempted ?: 0
                    val redSigPct = if (redSigAttempted > 0) ((redSigLanded.toFloat() / redSigAttempted) * 100).roundToInt() else 0

                    val blueSigLanded = blueStat?.sigStrikesLanded ?: 0
                    val blueSigAttempted = blueStat?.sigStrikesAttempted ?: 0
                    val blueSigPct = if (blueSigAttempted > 0) ((blueSigLanded.toFloat() / blueSigAttempted) * 100).roundToInt() else 0

                    MatchStatRow(
                        leftValue = "$redSigLanded/$redSigAttempted ($redSigPct%)",
                        leftNum = redSigLanded.toFloat(),
                        label = strings.matchStatsSigStrikes,
                        rightValue = "$blueSigLanded/$blueSigAttempted ($blueSigPct%)",
                        rightNum = blueSigLanded.toFloat()
                    )

                    // Total Strikes
                    val redTotalLanded = redStat?.totalStrikesLanded ?: 0
                    val redTotalAttempted = redStat?.totalStrikesAttempted ?: 0
                    val blueTotalLanded = blueStat?.totalStrikesLanded ?: 0
                    val blueTotalAttempted = blueStat?.totalStrikesAttempted ?: 0

                    MatchStatRow(
                        leftValue = "$redTotalLanded/$redTotalAttempted",
                        leftNum = redTotalLanded.toFloat(),
                        label = strings.matchStatsTotalStrikes,
                        rightValue = "$blueTotalLanded/$blueTotalAttempted",
                        rightNum = blueTotalLanded.toFloat()
                    )

                    // Takedowns
                    val redTdLanded = redStat?.takedownsLanded ?: 0
                    val redTdAttempted = redStat?.takedownsAttempted ?: 0
                    val blueTdLanded = blueStat?.takedownsLanded ?: 0
                    val blueTdAttempted = blueStat?.takedownsAttempted ?: 0

                    MatchStatRow(
                        leftValue = "$redTdLanded/$redTdAttempted",
                        leftNum = redTdLanded.toFloat(),
                        label = strings.matchStatsTakedowns,
                        rightValue = "$blueTdLanded/$blueTdAttempted",
                        rightNum = blueTdLanded.toFloat()
                    )

                    // Control Time
                    val redCtrl = redStat?.controlTimeSeconds ?: 0
                    val blueCtrl = blueStat?.controlTimeSeconds ?: 0

                    MatchStatRow(
                        leftValue = formatControlTime(redCtrl),
                        leftNum = redCtrl.toFloat(),
                        label = strings.matchStatsControlTime,
                        rightValue = formatControlTime(blueCtrl),
                        rightNum = blueCtrl.toFloat()
                    )

                    // Submissions & Reversals
                    val redSub = redStat?.submissionAttempts ?: 0
                    val blueSub = blueStat?.submissionAttempts ?: 0
                    MatchStatRow(
                        leftValue = redSub.toString(),
                        leftNum = redSub.toFloat(),
                        label = strings.matchStatsSubAttempts,
                        rightValue = blueSub.toString(),
                        rightNum = blueSub.toFloat()
                    )

                    val redRev = redStat?.reversals ?: 0
                    val blueRev = blueStat?.reversals ?: 0
                    MatchStatRow(
                        leftValue = redRev.toString(),
                        leftNum = redRev.toFloat(),
                        label = strings.matchStatsReversals,
                        rightValue = blueRev.toString(),
                        rightNum = blueRev.toFloat()
                    )
                }
            }
        }

        // 2. Strikes by Target Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.fightItemBackground)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = strings.matchStatsByTarget,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = colors.textSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Head
                    val redHeadLanded = redStat?.headLanded ?: 0
                    val redHeadAttempted = redStat?.headAttempted ?: 0
                    val blueHeadLanded = blueStat?.headLanded ?: 0
                    val blueHeadAttempted = blueStat?.headAttempted ?: 0

                    MatchStatRow(
                        leftValue = "$redHeadLanded/$redHeadAttempted",
                        leftNum = redHeadLanded.toFloat(),
                        label = strings.matchStatsHead,
                        rightValue = "$blueHeadLanded/$blueHeadAttempted",
                        rightNum = blueHeadLanded.toFloat()
                    )

                    // Body
                    val redBodyLanded = redStat?.bodyLanded ?: 0
                    val redBodyAttempted = redStat?.bodyAttempted ?: 0
                    val blueBodyLanded = blueStat?.bodyLanded ?: 0
                    val blueBodyAttempted = blueStat?.bodyAttempted ?: 0

                    MatchStatRow(
                        leftValue = "$redBodyLanded/$redBodyAttempted",
                        leftNum = redBodyLanded.toFloat(),
                        label = strings.matchStatsBody,
                        rightValue = "$blueBodyLanded/$blueBodyAttempted",
                        rightNum = blueBodyLanded.toFloat()
                    )

                    // Leg
                    val redLegLanded = redStat?.legLanded ?: 0
                    val redLegAttempted = redStat?.legAttempted ?: 0
                    val blueLegLanded = blueStat?.legLanded ?: 0
                    val blueLegAttempted = blueStat?.legAttempted ?: 0

                    MatchStatRow(
                        leftValue = "$redLegLanded/$redLegAttempted",
                        leftNum = redLegLanded.toFloat(),
                        label = strings.matchStatsLeg,
                        rightValue = "$blueLegLanded/$blueLegAttempted",
                        rightNum = blueLegLanded.toFloat()
                    )
                }
            }
        }

        // 3. Strikes by Position Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.fightItemBackground)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                    Text(
                        text = strings.matchStatsByPosition,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = colors.textSecondary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Distance
                        val redDistLanded = redStat?.distanceLanded ?: 0
                        val redDistAttempted = redStat?.distanceAttempted ?: 0
                        val blueDistLanded = blueStat?.distanceLanded ?: 0
                        val blueDistAttempted = blueStat?.distanceAttempted ?: 0

                        MatchStatRow(
                            leftValue = "$redDistLanded/$redDistAttempted",
                            leftNum = redDistLanded.toFloat(),
                            label = strings.matchStatsDistance,
                            rightValue = "$blueDistLanded/$blueDistAttempted",
                            rightNum = blueDistLanded.toFloat()
                        )

                        // Clinch
                        val redClinchLanded = redStat?.clinchLanded ?: 0
                        val redClinchAttempted = redStat?.clinchAttempted ?: 0
                        val blueClinchLanded = blueStat?.clinchLanded ?: 0
                        val blueClinchAttempted = blueStat?.clinchAttempted ?: 0

                        MatchStatRow(
                            leftValue = "$redClinchLanded/$redClinchAttempted",
                            leftNum = redClinchLanded.toFloat(),
                            label = strings.matchStatsClinch,
                            rightValue = "$blueClinchLanded/$blueClinchAttempted",
                            rightNum = blueClinchLanded.toFloat()
                        )

                        // Ground
                        val redGroundLanded = redStat?.groundLanded ?: 0
                        val redGroundAttempted = redStat?.groundAttempted ?: 0
                        val blueGroundLanded = blueStat?.groundLanded ?: 0
                        val blueGroundAttempted = blueStat?.groundAttempted ?: 0

                        MatchStatRow(
                            leftValue = "$redGroundLanded/$redGroundAttempted",
                            leftNum = redGroundLanded.toFloat(),
                            label = strings.matchStatsGround,
                            rightValue = "$blueGroundLanded/$blueGroundAttempted",
                            rightNum = blueGroundLanded.toFloat()
                        )
                    }
                }
            }
        }
    }

@Composable
private fun MatchStatRow(
    leftValue: String,
    leftNum: Float,
    label: String,
    rightValue: String,
    rightNum: Float,
    lowerIsBetter: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    val trackColor = colors.statComparisonTrack
    val mutedBarColor = colors.statComparisonMuted

    val (isLeftWinner, isRightWinner) = when {
        leftNum == rightNum -> Pair(false, false)
        lowerIsBetter -> {
            if (leftNum > 0f && rightNum > 0f) {
                Pair(leftNum < rightNum, rightNum < leftNum)
            } else Pair(false, false)
        }
        else -> Pair(leftNum > rightNum, rightNum > leftNum)
    }

    val (leftFraction, rightFraction) = calcProportions(leftNum, rightNum)

    val leftTextColor = if (isLeftWinner) colors.radarRed else colors.textSecondary
    val leftTextWeight = if (isLeftWinner) FontWeight.SemiBold else FontWeight.Normal
    val leftBarColor = if (isLeftWinner) colors.radarRed else mutedBarColor

    val rightTextColor = if (isRightWinner) colors.radarBlue else colors.textSecondary
    val rightTextWeight = if (isRightWinner) FontWeight.SemiBold else FontWeight.Normal
    val rightBarColor = if (isRightWinner) colors.radarBlue else mutedBarColor

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = leftValue,
                style = MaterialTheme.typography.bodyMedium,
                color = leftTextColor,
                fontWeight = leftTextWeight,
                textAlign = TextAlign.Start,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.weight(1.2f)
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1.6f)
            )

            Text(
                text = rightValue,
                style = MaterialTheme.typography.bodyMedium,
                color = rightTextColor,
                fontWeight = rightTextWeight,
                textAlign = TextAlign.End,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.weight(1.2f)
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.5.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Red Bar (Anchor Left)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(1.75.dp))
                    .background(trackColor),
                contentAlignment = Alignment.CenterStart
            ) {
                if (leftFraction > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(leftFraction.coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .background(leftBarColor)
                    )
                }
            }

            // Blue Bar (Anchor Right)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(1.75.dp))
                    .background(trackColor),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (rightFraction > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(rightFraction.coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .background(rightBarColor)
                    )
                }
            }
        }
    }
}

private fun calcProportions(val1: Float, val2: Float): Pair<Float, Float> {
    val total = val1 + val2
    if (total <= 0f) return Pair(0f, 0f)
    return Pair(val1 / total, val2 / total)
}

private fun formatControlTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "$m:${if (s < 10) "0$s" else s.toString()}"
}
