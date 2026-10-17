package com.berkekucuk.mmaapp.presentation.screens.event_detail

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.berkekucuk.mmaapp.domain.model.Fight
import com.berkekucuk.mmaapp.presentation.components.FightItem
import com.berkekucuk.mmaapp.presentation.components.ListContainer

@Composable
fun FightsContainer(
    fights: List<Fight>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onFightClick: (String) -> Unit,
    emptyMessage: String,
    listState: LazyListState,
    eventDate: String?,
    eventVenueAndLocation: String?,
    isLive: Boolean = false,
    extraBottomPadding: Dp = 0.dp,
) {

    ListContainer(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        listState = listState,
        contentPadding = PaddingValues(top = 8.dp),
        verticalSpacing = 0.dp,
        extraBottomPadding = extraBottomPadding,
    ) {
        item(contentType = "EventHeader") {
            Column {
                EventInfoCard(
                    date = eventDate,
                    venueAndLocation = eventVenueAndLocation,
                    isLive = isLive
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (fights.isEmpty()) {
            item(
                contentType = "EmptyState"
            ) {
                Box(
                    modifier = Modifier.fillParentMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emptyMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppTheme.colors.textSecondary
                    )
                }
            }
        } else {
            item(
                contentType = "FightsContainer"
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppTheme.colors.fightItemBackground)
                ) {
                    fights.forEachIndexed { index, fight ->
                        FightItem(
                            fight = fight,
                            modifier = Modifier.height(108.dp),
                            onClick = { onFightClick(fight.fightId) }
                        )
                        if (index < fights.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                thickness = 1.dp,
                                color = AppTheme.colors.dividerColor
                            )
                        }
                    }
                }
            }
        }
    }
}
