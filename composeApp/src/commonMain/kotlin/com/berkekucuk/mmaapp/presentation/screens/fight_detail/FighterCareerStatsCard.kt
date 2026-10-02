package com.berkekucuk.mmaapp.presentation.screens.fight_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.berkekucuk.mmaapp.domain.model.Fighter
import kotlin.math.roundToInt

@Composable
fun FighterCareerStatsSection(
    redFighter: Fighter?,
    blueFighter: Fighter?,
    modifier: Modifier = Modifier,
) {
    val hasFinishingStats = redFighter?.hasFinishingStats == true || blueFighter?.hasFinishingStats == true
    val hasStrikingStats = redFighter?.hasStrikingStats == true || blueFighter?.hasStrikingStats == true
    val hasGrapplingStats = redFighter?.hasGrapplingStats == true || blueFighter?.hasGrapplingStats == true

    if (!hasFinishingStats && !hasStrikingStats && !hasGrapplingStats) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (hasFinishingStats) {
            FinishingStatsCard(
                redFighter = redFighter,
                blueFighter = blueFighter
            )
        }

        if (hasStrikingStats) {
            StrikingStatsCard(
                redFighter = redFighter,
                blueFighter = blueFighter
            )
        }

        if (hasGrapplingStats) {
            GrapplingStatsCard(
                redFighter = redFighter,
                blueFighter = blueFighter
            )
        }
    }
}

@Composable
fun FinishingStatsCard(
    redFighter: Fighter?,
    blueFighter: Fighter?,
    modifier: Modifier = Modifier,
) {
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current

    // Win Rate
    val (redWinRateText, redWinRateFraction) = formatPercent(redFighter?.winRate)
    val (blueWinRateText, blueWinRateFraction) = formatPercent(blueFighter?.winRate)

    // KO/TKO Rate
    val (redKoRateText, redKoRateFraction) = formatPercent(redFighter?.koTkoRate)
    val (blueKoRateText, blueKoRateFraction) = formatPercent(blueFighter?.koTkoRate)

    // Submission Rate
    val (redSubRateText, redSubRateFraction) = formatPercent(redFighter?.submissionRate)
    val (blueSubRateText, blueSubRateFraction) = formatPercent(blueFighter?.submissionRate)

    Box(
        modifier = modifier
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
                text = strings.statsFinishesHeader,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = colors.textSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            StatComparisonRow(
                leftValue = redWinRateText,
                leftNum = redFighter?.winRate,
                leftFraction = redWinRateFraction,
                label = strings.statsWinRate,
                rightValue = blueWinRateText,
                rightNum = blueFighter?.winRate,
                rightFraction = blueWinRateFraction
            )

            StatComparisonRow(
                leftValue = redKoRateText,
                leftNum = redFighter?.koTkoRate,
                leftFraction = redKoRateFraction,
                label = strings.statsKoTkoRate,
                rightValue = blueKoRateText,
                rightNum = blueFighter?.koTkoRate,
                rightFraction = blueKoRateFraction
            )

            StatComparisonRow(
                leftValue = redSubRateText,
                leftNum = redFighter?.submissionRate,
                leftFraction = redSubRateFraction,
                label = strings.statsSubmissionRate,
                rightValue = blueSubRateText,
                rightNum = blueFighter?.submissionRate,
                rightFraction = blueSubRateFraction
            )
        }
    }
}

@Composable
fun StrikingStatsCard(
    redFighter: Fighter?,
    blueFighter: Fighter?,
    modifier: Modifier = Modifier,
) {
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current

    // Landed per min (SLpM)
    val maxLanded = maxOf(redFighter?.slpm ?: 0f, blueFighter?.slpm ?: 0f, 8f)
    val redLandedFraction = (redFighter?.slpm ?: 0f) / maxLanded
    val blueLandedFraction = (blueFighter?.slpm ?: 0f) / maxLanded

    // Significant Strikes Accuracy
    val (redStrAccText, redStrAccFraction) = formatPercent(redFighter?.strAcc)
    val (blueStrAccText, blueStrAccFraction) = formatPercent(blueFighter?.strAcc)

    // Absorbed per min (SApM)
    val maxAbsorbed = maxOf(redFighter?.sapm ?: 0f, blueFighter?.sapm ?: 0f, 6f)
    val redAbsorbedFraction = (redFighter?.sapm ?: 0f) / maxAbsorbed
    val blueAbsorbedFraction = (blueFighter?.sapm ?: 0f) / maxAbsorbed

    // Defense (Str Def)
    val (redStrDefText, redStrDefFraction) = formatPercent(redFighter?.strDef)
    val (blueStrDefText, blueStrDefFraction) = formatPercent(blueFighter?.strDef)

    Box(
        modifier = modifier
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
                text = strings.statsStrikingHeader,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = colors.textSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            StatComparisonRow(
                leftValue = formatRate(redFighter?.slpm),
                leftNum = redFighter?.slpm,
                leftFraction = redLandedFraction,
                label = strings.statsLandedPerMin,
                rightValue = formatRate(blueFighter?.slpm),
                rightNum = blueFighter?.slpm,
                rightFraction = blueLandedFraction
            )

            StatComparisonRow(
                leftValue = redStrAccText,
                leftNum = redFighter?.strAcc,
                leftFraction = redStrAccFraction,
                label = strings.statsSignificantStrikes,
                rightValue = blueStrAccText,
                rightNum = blueFighter?.strAcc,
                rightFraction = blueStrAccFraction
            )

            StatComparisonRow(
                leftValue = formatRate(redFighter?.sapm),
                leftNum = redFighter?.sapm,
                leftFraction = redAbsorbedFraction,
                label = strings.statsAbsorbedPerMin,
                rightValue = formatRate(blueFighter?.sapm),
                rightNum = blueFighter?.sapm,
                rightFraction = blueAbsorbedFraction,
                lowerIsBetter = true
            )

            StatComparisonRow(
                leftValue = redStrDefText,
                leftNum = redFighter?.strDef,
                leftFraction = redStrDefFraction,
                label = strings.statsDefense,
                rightValue = blueStrDefText,
                rightNum = blueFighter?.strDef,
                rightFraction = blueStrDefFraction
            )
        }
    }
}

@Composable
fun GrapplingStatsCard(
    redFighter: Fighter?,
    blueFighter: Fighter?,
    modifier: Modifier = Modifier,
) {
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current

    // Takedown Avg per 15 min
    val maxTd = maxOf(redFighter?.tdAvg ?: 0f, blueFighter?.tdAvg ?: 0f, 4f)
    val redTdFraction = (redFighter?.tdAvg ?: 0f) / maxTd
    val blueTdFraction = (blueFighter?.tdAvg ?: 0f) / maxTd

    // Takedown Accuracy
    val (redTdAccText, redTdAccFraction) = formatPercent(redFighter?.tdAcc)
    val (blueTdAccText, blueTdAccFraction) = formatPercent(blueFighter?.tdAcc)

    // Takedown Defense
    val (redTdDefText, redTdDefFraction) = formatPercent(redFighter?.tdDef)
    val (blueTdDefText, blueTdDefFraction) = formatPercent(blueFighter?.tdDef)

    // Submission Avg per 15 min
    val maxSub = maxOf(redFighter?.subAvg ?: 0f, blueFighter?.subAvg ?: 0f, 3f)
    val redSubFraction = (redFighter?.subAvg ?: 0f) / maxSub
    val blueSubFraction = (blueFighter?.subAvg ?: 0f) / maxSub

    Box(
        modifier = modifier
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
                text = strings.statsGrapplingHeader,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = colors.textSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            StatComparisonRow(
                leftValue = formatRate(redFighter?.tdAvg),
                leftNum = redFighter?.tdAvg,
                leftFraction = redTdFraction,
                label = strings.statsTakedownAvg,
                rightValue = formatRate(blueFighter?.tdAvg),
                rightNum = blueFighter?.tdAvg,
                rightFraction = blueTdFraction
            )

            StatComparisonRow(
                leftValue = redTdAccText,
                leftNum = redFighter?.tdAcc,
                leftFraction = redTdAccFraction,
                label = strings.statsTakedownAccuracy,
                rightValue = blueTdAccText,
                rightNum = blueFighter?.tdAcc,
                rightFraction = blueTdAccFraction
            )

            StatComparisonRow(
                leftValue = redTdDefText,
                leftNum = redFighter?.tdDef,
                leftFraction = redTdDefFraction,
                label = strings.statsTakedownDefense,
                rightValue = blueTdDefText,
                rightNum = blueFighter?.tdDef,
                rightFraction = blueTdDefFraction
            )

            StatComparisonRow(
                leftValue = formatRate(redFighter?.subAvg),
                leftNum = redFighter?.subAvg,
                leftFraction = redSubFraction,
                label = strings.statsSubmissionAvg,
                rightValue = formatRate(blueFighter?.subAvg),
                rightNum = blueFighter?.subAvg,
                rightFraction = blueSubFraction
            )
        }
    }
}

@Composable
fun StatComparisonRow(
    leftValue: String,
    leftNum: Float?,
    leftFraction: Float,
    label: String,
    rightValue: String,
    rightNum: Float?,
    rightFraction: Float,
    lowerIsBetter: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    val trackColor = colors.statComparisonTrack
    val mutedBarColor = colors.statComparisonMuted

    val (isLeftWinner, isRightWinner) = when {
        leftNum == null || rightNum == null -> Pair(false, false)
        leftNum == rightNum -> Pair(false, false)
        lowerIsBetter -> {
            if (leftNum > 0f && rightNum > 0f) {
                Pair(leftNum < rightNum, rightNum < leftNum)
            } else Pair(false, false)
        }
        else -> Pair(leftNum > rightNum, rightNum > leftNum)
    }

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
                modifier = Modifier.weight(0.65f)
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(2.7f)
            )

            Text(
                text = rightValue,
                style = MaterialTheme.typography.bodyMedium,
                color = rightTextColor,
                fontWeight = rightTextWeight,
                textAlign = TextAlign.End,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.weight(0.65f)
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.5.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Left Bar (Red corner)
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

            // Right Bar (Blue corner)
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

private val Fighter.hasFinishingStats: Boolean
    get() = winRate > 0f || koTkoRate > 0f || submissionRate > 0f

private val Fighter.hasStrikingStats: Boolean
    get() = slpm > 0f || strAcc > 0f || sapm > 0f || strDef > 0f

private val Fighter.hasGrapplingStats: Boolean
    get() = tdAvg > 0f || tdAcc > 0f || tdDef > 0f || subAvg > 0f

private fun formatRate(value: Float?): String {
    if (value == null) return "—"
    if (value == 0f) return "0"
    val rounded = (value * 100).roundToInt() / 100f
    return if (rounded % 1f == 0f) rounded.toInt().toString() else rounded.toString()
}

private fun formatPercent(value: Float?): Pair<String, Float> {
    if (value == null) return Pair("—", 0f)
    val fraction = (if (value <= 1.0f) value else value / 100f).coerceIn(0f, 1f)
    val percentInt = (fraction * 100).roundToInt()
    return Pair("$percentInt%", fraction)
}
