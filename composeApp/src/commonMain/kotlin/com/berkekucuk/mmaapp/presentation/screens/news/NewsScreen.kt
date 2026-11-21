package com.berkekucuk.mmaapp.presentation.screens.news

import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.berkekucuk.mmaapp.presentation.components.PaginationControls
import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.berkekucuk.mmaapp.core.utils.rememberLocalizedDateStrings
import com.berkekucuk.mmaapp.core.utils.toRelativeTime
import com.berkekucuk.mmaapp.presentation.components.AppSnackbar
import com.berkekucuk.mmaapp.presentation.components.AppSnackbarEffect
import com.berkekucuk.mmaapp.presentation.components.ListContainer
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

@Composable
fun NewsScreenRoot(
    viewModel: NewsViewModel = koinViewModel(),
    onNavigateToNewsDetail: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            when (event) {
                is NewsNavigationEvent.ToNewsDetail -> onNavigateToNewsDetail(event.articleId)
            }
        }
    }

    NewsScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    state: NewsUiState,
    onAction: (NewsUiAction) -> Unit
) {
    // Theme & Resources
    val dateStrings = rememberLocalizedDateStrings()

    // Compose Core States
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    var lastScrolledPage by rememberSaveable { mutableStateOf(-1) }

    // UI Data & Definitions
    val errorMessage = AppTheme.strings.mapError(state.error)
    val now = remember(state.articles) { Clock.System.now() }

    LaunchedEffect(state.articles) {
        if (lastScrolledPage != state.currentPage) {
            listState.scrollToItem(0)
            lastScrolledPage = state.currentPage
        }
    }

    AppSnackbarEffect(
        message = errorMessage,
        snackbarHostState = snackbarHostState,
        actionLabel = AppTheme.strings.retry,
        onAction = { onAction(NewsUiAction.OnRefresh) },
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = AppTheme.colors.pagerBackground,
        contentWindowInsets = WindowInsets(0),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    AppSnackbar(
                        snackbarData = snackbarData,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            )
        },
        topBar = {
            Column(
                modifier = Modifier.background(AppTheme.colors.eventsTopBar)
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = AppTheme.strings.newsTitle.uppercase(),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        titleContentColor = AppTheme.colors.textPrimary
                    ),
                    scrollBehavior = scrollBehavior
                )
            }
        }
    ) { innerPadding ->
        ListContainer(
            isRefreshing = state.isRefreshing,
            onRefresh = { onAction(NewsUiAction.OnRefresh) },
            modifier = Modifier.padding(innerPadding),
            listState = listState,
            contentPadding = PaddingValues(top = 16.dp),
            verticalSpacing = 16.dp
        ) {
            if (state.articles.isEmpty()) {
                if (state.currentPage > 0) {
                    item(contentType = "EmptyPageControls") {
                        PaginationControls(
                            currentPage = state.currentPage,
                            canGoNext = state.canGoNext,
                            isRefreshing = state.isRefreshing,
                            onNextPage = { onAction(NewsUiAction.OnNextPage) },
                            onPreviousPage = { onAction(NewsUiAction.OnPreviousPage) }
                        )
                    }
                }
            } else {
                items(
                    items = state.articles,
                    key = { it.id },
                    contentType = { "NewsArticle" }
                ) { article ->
                    NewsArticleItem(
                        article = article,
                        timeText = article.publishedAt.toRelativeTime(
                            now = now,
                            justNow = AppTheme.strings.commentJustNow,
                            minutesAgo = AppTheme.strings.commentMinutesAgo,
                            hoursAgo = AppTheme.strings.commentHoursAgo,
                            daysAgo = AppTheme.strings.commentDaysAgo,
                            months = dateStrings.months,
                        ),
                        onClick = { onAction(NewsUiAction.OnArticleClicked(article.id)) }
                    )
                }

                item(key = "pagination_controls") {
                    PaginationControls(
                        currentPage = state.currentPage,
                        canGoNext = state.canGoNext,
                        isRefreshing = state.isRefreshing,
                        onNextPage = { onAction(NewsUiAction.OnNextPage) },
                        onPreviousPage = { onAction(NewsUiAction.OnPreviousPage) }
                    )
                }
            }
        }
    }
}
