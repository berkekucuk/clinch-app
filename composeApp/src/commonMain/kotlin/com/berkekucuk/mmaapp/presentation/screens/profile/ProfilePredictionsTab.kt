package com.berkekucuk.mmaapp.presentation.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
import com.berkekucuk.mmaapp.presentation.components.ListContainer

@Composable
fun ProfilePredictionsTab(
    state: ProfileUiState,
    onRefresh: () -> Unit,
    onPredictionClicked: (String) -> Unit,
    onNextPage: () -> Unit,
    onPreviousPage: () -> Unit,
    navBarBottomPadding: Dp,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current

    val predictions = state.predictions
    val activePredictions = remember(predictions) {
        predictions.filterNot { it.isCancelledOrFizzled }
    }
    val listState = rememberLazyListState()
    var lastScrolledPage by remember { mutableStateOf(-1) }

    LaunchedEffect(predictions) {
        if (lastScrolledPage != state.currentPage) {
            listState.scrollToItem(0)
            lastScrolledPage = state.currentPage
        }
    }

    ListContainer(
        isRefreshing = state.isRefreshing,
        onRefresh = onRefresh,
        listState = listState,
        contentPadding = PaddingValues(top = 16.dp),
        extraBottomPadding = navBarBottomPadding,
        modifier = modifier.fillMaxSize()
    ) {
        if (activePredictions.isEmpty()) {
            item(contentType = "EmptyStateAndControls") {
                Box(
                    modifier = Modifier.fillParentMaxSize()
                ) {
                    Text(
                        text = strings.emptyPredictionList,
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.textSecondary,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    if (state.currentPage > 0 || state.canGoNext) {
                        PaginationControls(
                            state = state,
                            onNextPage = onNextPage,
                            onPreviousPage = onPreviousPage,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        } else {
            items(
                items = activePredictions,
                key = { it.predictionId }
            ) { prediction ->
                PredictionCard(
                    prediction = prediction,
                    onClick = {
                        prediction.fight?.let { fight ->
                            onPredictionClicked(fight.fightId)
                        }
                    },
                )
            }

            item(key = "pagination_controls") {
                PaginationControls(
                    state = state,
                    onNextPage = onNextPage,
                    onPreviousPage = onPreviousPage
                )
            }
        }
    }
}

@Composable
private fun PaginationControls(
    state: ProfileUiState,
    onNextPage: () -> Unit,
    onPreviousPage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPreviousPage,
            enabled = state.currentPage > 0 && !state.isRefreshing
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous Page",
                tint = if (state.currentPage > 0 && !state.isRefreshing) colors.textPrimary else colors.textSecondary
            )
        }

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${state.currentPage + 1}",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary
            )
        }

        IconButton(
            onClick = onNextPage,
            enabled = state.canGoNext && !state.isRefreshing
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next Page",
                tint = if (state.canGoNext && !state.isRefreshing) colors.textPrimary else colors.textSecondary
            )
        }
    }
}
