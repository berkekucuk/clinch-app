package com.berkekucuk.mmaapp.presentation.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
import com.berkekucuk.mmaapp.core.utils.rememberLocalizedDateStrings
import com.berkekucuk.mmaapp.core.utils.toJoinedDate
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
    val colors = LocalAppColors.current
    val dateStrings = rememberLocalizedDateStrings()
    val formattedJoinedDate = remember(state.profile?.user?.createdAt, dateStrings.months) {
        state.profile?.user?.createdAt?.toJoinedDate(dateStrings.months)
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ListContainer(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
            contentPadding = PaddingValues(top = 16.dp),
            extraBottomPadding = 0.dp,
            modifier = Modifier.weight(1f)
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

        formattedJoinedDate?.let { date ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = navBarBottomPadding + 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = strings.profileJoinedDate(date),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}
