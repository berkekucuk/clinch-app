package com.berkekucuk.mmaapp.presentation.screens.fighter_detail

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
import com.berkekucuk.mmaapp.core.utils.calculateAgeAtDate
import com.berkekucuk.mmaapp.core.utils.formatDateOfBirth
import com.berkekucuk.mmaapp.domain.model.Fighter
import com.berkekucuk.mmaapp.presentation.components.ListContainer
import com.berkekucuk.mmaapp.core.utils.rememberLocalizedDateStrings
import kotlin.time.Clock

@Composable
fun FighterOverviewContainer(
    fighter: Fighter,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    extraBottomPadding: Dp = 0.dp,
) {
    val strings = LocalAppStrings.current
    val dateStrings = rememberLocalizedDateStrings()
    val weightClassDisplay = strings.weightClassDisplayName(fighter.weightClassId)

    val age = remember(fighter.dateOfBirth) {
        calculateAgeAtDate(fighter.dateOfBirth, Clock.System.now())
    }

    val formattedDob = remember(fighter.dateOfBirth, dateStrings) {
        formatDateOfBirth(fighter.dateOfBirth, dateStrings.months)
    }

    ListContainer(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        contentPadding = PaddingValues(top = 8.dp),
        verticalSpacing = 8.dp,
        extraBottomPadding = extraBottomPadding,
    ) {
        item(key = "record_card") {
            FighterRecordCard(record = fighter.record)
        }

        item(key = "info_card") {
            FighterInfoCard(
                fighter = fighter,
                age = age,
                formattedDob = formattedDob,
                weightClassDisplay = weightClassDisplay,
            )
        }

        if (fighter.hasFinishingStats) {
            item(key = "finishing_stats_card") {
                FighterFinishingStatsCard(fighter = fighter)
            }
        }

        if (fighter.hasStrikingStats) {
            item(key = "striking_stats_card") {
                FighterStrikingStatsCard(fighter = fighter)
            }
        }

        if (fighter.hasGrapplingStats) {
            item(key = "grappling_stats_card") {
                FighterGrapplingStatsCard(fighter = fighter)
            }
        }
    }
}
