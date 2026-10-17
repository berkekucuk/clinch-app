package com.berkekucuk.mmaapp.presentation.screens.fighter_detail

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.background
import com.berkekucuk.mmaapp.presentation.components.appClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berkekucuk.mmaapp.core.utils.toShortDate
import com.berkekucuk.mmaapp.core.utils.toYear
import com.berkekucuk.mmaapp.domain.enums.Result
import com.berkekucuk.mmaapp.domain.model.Fight
import com.berkekucuk.mmaapp.presentation.components.FighterPortrait
import com.berkekucuk.mmaapp.core.utils.rememberLocalizedDateStrings

@Composable
fun FightHistoryRow(
    fight: Fight,
    fighterId: String,
    onClick: () -> Unit,
) {
    val dateStrings = rememberLocalizedDateStrings()
    val fighter = fight.participants.find { it.fighter.fighterId == fighterId }
    val opponent = fight.participants.find { it.fighter.fighterId != fighterId }
    val opponentFighter = opponent?.fighter
    val result = fighter?.result

    val badgeColor = when (result) {
        Result.PENDING -> AppTheme.colors.upcomingColor
        Result.WIN -> AppTheme.colors.winColor
        Result.LOSS -> AppTheme.colors.loseColor
        Result.DRAW -> AppTheme.colors.drawColor
        Result.NO_CONTEST -> AppTheme.colors.noContestColor
        else -> AppTheme.colors.cardBorder
    }

    val badgeScriptColor = when(result) {
        Result.PENDING -> AppTheme.colors.upcomingColor2
        Result.WIN -> AppTheme.colors.winColor2
        Result.LOSS -> AppTheme.colors.loseColor2
        Result.DRAW -> AppTheme.colors.drawColor2
        Result.NO_CONTEST -> AppTheme.colors.noContestColor2
        else -> AppTheme.colors.cardBorder
    }

    val resultLetter = when (result) {
        Result.WIN -> AppTheme.strings.fighterDetailResultWin
        Result.LOSS -> AppTheme.strings.fighterDetailResultLoss
        Result.DRAW -> AppTheme.strings.fighterDetailResultDraw
        Result.NO_CONTEST -> AppTheme.strings.fighterDetailResultNoContest
        else -> AppTheme.strings.fighterDetailResultPending
    }

    val methodAbbr = abbreviateMethod(fight.methodType)
    val year = fight.eventDate?.toYear()
    val shortDate = fight.eventDate?.toShortDate(dateStrings.months)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .appClickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .fillMaxHeight()
                .background(badgeColor),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = resultLetter,
                    color = badgeScriptColor,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                if (methodAbbr != null && result != Result.DRAW && result != Result.NO_CONTEST) {
                    Text(
                        text = methodAbbr,
                        color = badgeScriptColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .background(color = AppTheme.colors.fightItemBackground)
                .fillMaxHeight()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FighterPortrait(
                name = opponentFighter?.name,
                imageUrl = opponentFighter?.imageUrl,
                countryCode = opponentFighter?.countryCode,
                record = fight.eventName,
                alignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                imageSize = 44.dp,
                flagWidth = 16.dp,
                flagHeight = 10.dp,
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = year ?: "",
                    color = AppTheme.colors.textPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
                if (shortDate != null) {
                    Text(
                        text = shortDate,
                        color = AppTheme.colors.textSecondary,
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}

private fun abbreviateMethod(methodType: String?): String? {
    if (methodType.isNullOrBlank()) return null
    return when {
        methodType.contains("decision", ignoreCase = true) -> "DEC"
        methodType.contains("tko", ignoreCase = true) || methodType.contains("ko", ignoreCase = true) -> "KO/TKO"
        methodType.contains("sub", ignoreCase = true) -> "SUB"
        else -> methodType.take(3).uppercase()
    }
}

