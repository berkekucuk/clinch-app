package com.berkekucuk.mmaapp.presentation.screens.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berkekucuk.mmaapp.core.presentation.colors.AppColors
import com.berkekucuk.mmaapp.domain.model.User
import com.berkekucuk.mmaapp.presentation.components.ListContainer
import com.berkekucuk.mmaapp.presentation.components.PaginationControls
import com.berkekucuk.mmaapp.presentation.screens.ranking_detail.RankedFighterRow

@Composable
fun LeaderboardContainer(
    isOverall: Boolean,
    leaderboard: List<User>,
    isRefreshing: Boolean,
    currentPage: Int,
    canGoNext: Boolean,
    currentUserId: String?,
    listState: LazyListState,
    onAction: (LeaderboardUiAction) -> Unit,
    navBarBottomPadding: Dp,
    colors: AppColors
) {
    ListContainer(
        isRefreshing = isRefreshing,
        onRefresh = { onAction(LeaderboardUiAction.OnRefresh) },
        listState = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 8.dp),
        verticalSpacing = 0.dp,
        extraBottomPadding = navBarBottomPadding,
    ) {
        if (leaderboard.isEmpty()) {
            item {
                Box(modifier = Modifier.fillParentMaxSize()) {
                    if (isOverall && (currentPage > 0 || canGoNext)) {
                        PaginationControls(
                            currentPage = currentPage,
                            canGoNext = canGoNext,
                            isRefreshing = isRefreshing,
                            onNextPage = { onAction(LeaderboardUiAction.OnNextPage) },
                            onPreviousPage = { onAction(LeaderboardUiAction.OnPreviousPage) },
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        } else {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.fightItemBackground)
                ) {
                    leaderboard.forEachIndexed { index, user ->
                        val isCurrentUser = user.id == currentUserId
                        val pageMultiplier = if (isOverall) currentPage else 0
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(if (isCurrentUser) Modifier.background(colors.winnerFrame.copy(alpha = 0.15f)) else Modifier)
                                .padding(horizontal = 12.dp)
                        ) {
                            RankedFighterRow(
                                rankNumber = (pageMultiplier * LeaderboardViewModel.PAGE_SIZE) + index + 1,
                                name = user.fullName ?: user.username ?: "Unknown",
                                record = user.username?.let { "@$it" } ?: "",
                                imageUrl = user.avatarUrl ?: "",
                                countryCode = null,
                                trailingContent = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(end = 4.dp)
                                    ) {
                                        Text(
                                            text = user.points.toString(),
                                            color = colors.winnerFrame,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.EmojiEvents,
                                            contentDescription = null,
                                            tint = colors.winnerFrame,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                },
                                onFighterClicked = { onAction(LeaderboardUiAction.OnUserClicked(user.id)) }
                            )

                            if (index < leaderboard.lastIndex) {
                                HorizontalDivider(
                                    color = colors.dividerColor,
                                    thickness = 0.8.dp,
                                )
                            }
                        }
                    }
                }
            }

            if (isOverall) {
                item(key = "pagination_controls") {
                    PaginationControls(
                        currentPage = currentPage,
                        canGoNext = canGoNext,
                        isRefreshing = isRefreshing,
                        onNextPage = { onAction(LeaderboardUiAction.OnNextPage) },
                        onPreviousPage = { onAction(LeaderboardUiAction.OnPreviousPage) }
                    )
                }
            }
        }
    }
}
