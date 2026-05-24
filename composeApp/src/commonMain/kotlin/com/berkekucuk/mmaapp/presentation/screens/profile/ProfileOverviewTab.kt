package com.berkekucuk.mmaapp.presentation.screens.profile

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
import com.berkekucuk.mmaapp.domain.model.toRankedFighter
import com.berkekucuk.mmaapp.presentation.components.ListContainer
import com.berkekucuk.mmaapp.presentation.screens.rankings.WeightClassCard

@Composable
fun ProfileOverviewTab(
    state: ProfileUiState,
    onRefresh: () -> Unit,
    onInteractionListClicked: (String) -> Unit,
    navBarBottomPadding: Dp,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    ListContainer(
        isRefreshing = state.isRefreshing,
        onRefresh = onRefresh,
        contentPadding = PaddingValues(top = 16.dp),
        extraBottomPadding = navBarBottomPadding,
        modifier = modifier
    ) {
        item {
            WeightClassCard(
                weightClassName = strings.toUpperCase(strings.profileFavoriteFighters),
                champion = state.profile?.topFavorite?.toRankedFighter(),
                onWeightClassClicked = { onInteractionListClicked("favorite") },
            )
        }
        item {
            WeightClassCard(
                weightClassName = strings.toUpperCase(strings.profileGoatFighters),
                champion = state.profile?.topGoat?.toRankedFighter(),
                onWeightClassClicked = { onInteractionListClicked("goat") },
            )
        }
        item {
            WeightClassCard(
                weightClassName = strings.toUpperCase(strings.profileHatedFighters),
                champion = state.profile?.topHated?.toRankedFighter(),
                onWeightClassClicked = { onInteractionListClicked("hated") },
            )
        }
    }
}
