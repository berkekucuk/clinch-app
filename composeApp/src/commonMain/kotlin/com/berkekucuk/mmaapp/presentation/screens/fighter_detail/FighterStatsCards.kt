package com.berkekucuk.mmaapp.presentation.screens.fighter_detail

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
fun FighterFinishingStatsCard(
    fighter: Fighter,
    modifier: Modifier = Modifier,
) {
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current

    val (winRateText, winRateFraction) = formatPercent(fighter.winRate)
    val (koRateText, koRateFraction) = formatPercent(fighter.koTkoRate)
    val (subRateText, subRateFraction) = formatPercent(fighter.submissionRate)

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

            SingleStatRow(
                label = strings.statsWinRate,
                value = winRateText,
                fraction = winRateFraction,
            )

            SingleStatRow(
                label = strings.statsKoTkoRate,
                value = koRateText,
                fraction = koRateFraction,
            )

            SingleStatRow(
                label = strings.statsSubmissionRate,
                value = subRateText,
                fraction = subRateFraction,
            )
        }
    }
}

@Composable
fun FighterStrikingStatsCard(
    fighter: Fighter,
    modifier: Modifier = Modifier,
) {
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current

    val landedFraction = (fighter.slpm / 8f).coerceIn(0f, 1f)
    val (strAccText, strAccFraction) = formatPercent(fighter.strAcc)
    val absorbedFraction = (fighter.sapm / 6f).coerceIn(0f, 1f)
    val (strDefText, strDefFraction) = formatPercent(fighter.strDef)

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

            SingleStatRow(
                label = strings.statsLandedPerMin,
                value = formatRate(fighter.slpm),
                fraction = landedFraction,
            )

            SingleStatRow(
                label = strings.statsSignificantStrikes,
                value = strAccText,
                fraction = strAccFraction,
            )

            SingleStatRow(
                label = strings.statsAbsorbedPerMin,
                value = formatRate(fighter.sapm),
                fraction = absorbedFraction,
            )

            SingleStatRow(
                label = strings.statsDefense,
                value = strDefText,
                fraction = strDefFraction,
            )
        }
    }
}

@Composable
fun FighterGrapplingStatsCard(
    fighter: Fighter,
    modifier: Modifier = Modifier,
) {
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current

    val tdAvgFraction = (fighter.tdAvg / 5f).coerceIn(0f, 1f)
    val (tdAccText, tdAccFraction) = formatPercent(fighter.tdAcc)
    val (tdDefText, tdDefFraction) = formatPercent(fighter.tdDef)
    val subAvgFraction = (fighter.subAvg / 3f).coerceIn(0f, 1f)

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

            SingleStatRow(
                label = strings.statsTakedownAvg,
                value = formatRate(fighter.tdAvg),
                fraction = tdAvgFraction,
            )

            SingleStatRow(
                label = strings.statsSignificantStrikes,
                value = tdAccText,
                fraction = tdAccFraction,
            )

            SingleStatRow(
                label = strings.statsDefense,
                value = tdDefText,
                fraction = tdDefFraction,
            )

            SingleStatRow(
                label = strings.statsSubmissionAvg,
                value = formatRate(fighter.subAvg),
                fraction = subAvgFraction,
            )
        }
    }
}

@Composable
fun SingleStatRow(
    label: String,
    value: String,
    fraction: Float,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    val trackColor = colors.statComparisonTrack
    val barColor = colors.statSingleBar

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false).padding(end = 8.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
            )
        }

        Spacer(Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.5.dp)
                .clip(RoundedCornerShape(1.75.dp))
                .background(trackColor),
            contentAlignment = Alignment.CenterStart
        ) {
            if (fraction > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(1.75.dp))
                        .background(barColor)
                )
            }
        }
    }
}

val Fighter.hasFinishingStats: Boolean
    get() = winRate > 0f || koTkoRate > 0f || submissionRate > 0f

val Fighter.hasStrikingStats: Boolean
    get() = slpm > 0f || strAcc > 0f || sapm > 0f || strDef > 0f

val Fighter.hasGrapplingStats: Boolean
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
