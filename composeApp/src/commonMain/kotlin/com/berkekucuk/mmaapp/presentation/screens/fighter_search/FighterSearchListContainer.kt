package com.berkekucuk.mmaapp.presentation.screens.fighter_search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.domain.model.Fighter
import com.berkekucuk.mmaapp.presentation.components.ListContainer

@Composable
fun FighterSearchListContainer(
    fighters: List<Fighter>,
    addingInteractionFighterId: String? = null,
    onFighterClicked: (String) -> Unit,
) {
    val navBarBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val colors = LocalAppColors.current
    val isAnyLoading = addingInteractionFighterId != null

    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            focusManager.clearFocus()
        }
    }

    ListContainer(
        isRefreshing = false,
        onRefresh = {},
        listState = listState,
        contentPadding = PaddingValues(top = 8.dp),
        verticalSpacing = 0.dp,
        extraBottomPadding = navBarBottomPadding,
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.fightItemBackground)
            ) {
                fighters.forEachIndexed { index, fighter ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        FighterSearchResultRow(
                            name = fighter.name,
                            imageUrl = fighter.imageUrl,
                            record = fighter.record.toString(),
                            countryCode = fighter.countryCode,
                            isLoading = fighter.fighterId == addingInteractionFighterId,
                            isClickable = !isAnyLoading,
                            onClick = { onFighterClicked(fighter.fighterId) }
                        )
                        if (index < fighters.lastIndex) {
                            HorizontalDivider(
                                color = colors.dividerColor,
                                thickness = 0.5.dp,
                            )
                        }
                    }
                }
            }
        }
    }
}
