package com.berkekucuk.mmaapp.presentation.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.saveable.rememberSaveable
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
import com.berkekucuk.mmaapp.presentation.components.ListContainer
import com.berkekucuk.mmaapp.presentation.components.PaginationControls

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
    var lastScrolledPage by rememberSaveable { mutableStateOf(-1) }

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
                            currentPage = state.currentPage,
                            canGoNext = state.canGoNext,
                            isRefreshing = state.isRefreshing,
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
                    currentPage = state.currentPage,
                    canGoNext = state.canGoNext,
                    isRefreshing = state.isRefreshing,
                    onNextPage = onNextPage,
                    onPreviousPage = onPreviousPage
                )
            }
        }
    }
}


