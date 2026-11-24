package com.berkekucuk.mmaapp.presentation.screens.fight_detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.berkekucuk.mmaapp.core.presentation.AppTheme
import com.berkekucuk.mmaapp.core.presentation.LocalOddsFormat
import com.berkekucuk.mmaapp.core.presentation.formatOdds
import com.berkekucuk.mmaapp.domain.model.Fight
import com.berkekucuk.mmaapp.domain.model.OddsMethod
import com.berkekucuk.mmaapp.domain.model.oddsFor
import com.berkekucuk.mmaapp.presentation.components.appClickable
import com.berkekucuk.mmaapp.presentation.components.label

fun Fight.pickOdds(fighterId: String, method: OddsMethod?, round: Int?): Int? =
    if (method == null) moneyline(fighterId) else odds.oddsFor(fighterId, method, round ?: 0)

fun Fight.pickMethods(fighterId: String): List<OddsMethod> =
    OddsMethod.METHODS.filter { method -> odds.oddsFor(fighterId, method) != null }

fun Fight.pickRounds(fighterId: String, method: OddsMethod): List<Int> =
    odds.filter { it.fighterId == fighterId && it.method == method && it.round > 0 }
        .map { it.round }
        .distinct()
        .sorted()

private data class PickOption(val label: String, val odds: Int?, val selected: Boolean, val onClick: () -> Unit)

@Composable
fun PredictionDetailSelector(
    fight: Fight,
    fighterId: String,
    selectedMethod: OddsMethod?,
    selectedRound: Int?,
    onMethodSelected: (OddsMethod?) -> Unit,
    onRoundSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val methods = fight.pickMethods(fighterId)
    if (methods.isEmpty()) return
    val rounds = if (selectedMethod == OddsMethod.KO_TKO || selectedMethod == OddsMethod.SUBMISSION) {
        fight.pickRounds(fighterId, selectedMethod)
    } else {
        emptyList()
    }

    val methodOptions = buildList {
        add(PickOption(AppTheme.strings.predictionAnyOption, fight.pickOdds(fighterId, null, null), selectedMethod == null) { onMethodSelected(null) })
        methods.forEach { method ->
            add(PickOption(method.label(), fight.pickOdds(fighterId, method, null), selectedMethod == method) { onMethodSelected(method) })
        }
    }

    Column(modifier = modifier) {
        OptionLabel(AppTheme.strings.predictionMethodLabel)
        OptionGrid(methodOptions)

        if (rounds.isNotEmpty() && selectedMethod != null) {
            val roundOptions = buildList {
                add(PickOption(AppTheme.strings.predictionAnyOption, fight.pickOdds(fighterId, selectedMethod, null), selectedRound == null) { onRoundSelected(null) })
                rounds.forEach { round ->
                    add(PickOption(AppTheme.strings.fightOddsRound(round), fight.pickOdds(fighterId, selectedMethod, round), selectedRound == round) { onRoundSelected(round) })
                }
            }
            OptionLabel(AppTheme.strings.predictionRoundLabel, modifier = Modifier.padding(top = 12.dp))
            OptionGrid(roundOptions)
        }
    }
}

@Composable
private fun OptionGrid(options: List<PickOption>) {
    val columns = if (options.size <= 4) 2 else 3

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.chunked(columns).forEach { rowOptions ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowOptions.forEach { option ->
                    OptionChip(option = option, modifier = Modifier.weight(1f))
                }
                repeat(columns - rowOptions.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun OptionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = AppTheme.colors.textSecondary,
        fontWeight = FontWeight.Medium,
        modifier = modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun OptionChip(
    option: PickOption,
    modifier: Modifier = Modifier,
) {
    val oddsFormat = LocalOddsFormat.current
    val shape = RoundedCornerShape(10.dp)
    val accent = AppTheme.colors.winnerFrame

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(shape)
            .background(if (option.selected) accent.copy(alpha = 0.12f) else Color.Transparent)
            .border(BorderStroke(1.dp, if (option.selected) accent else AppTheme.colors.cardBorder), shape)
            .appClickable(onClick = option.onClick)
            .padding(horizontal = 6.dp, vertical = 8.dp)
    ) {
        Text(
            text = option.label,
            style = MaterialTheme.typography.bodySmall,
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = option.odds?.formatOdds(oddsFormat) ?: "—",
            style = MaterialTheme.typography.bodySmall,
            color = if (option.selected) accent else AppTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}
