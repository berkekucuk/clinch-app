package com.berkekucuk.mmaapp.presentation.screens.rankings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
import com.berkekucuk.mmaapp.presentation.components.ErrorSnackbar
import com.berkekucuk.mmaapp.presentation.components.AppTabRow
import com.berkekucuk.mmaapp.presentation.components.SnackbarEffect
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.launch

@Composable
fun RankingsScreenRoot(
    viewModel: RankingViewModel = koinViewModel(),
    onNavigateToRankingDetail: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            when (event) {
                is RankingNavigationEvent.ToRankingDetail -> onNavigateToRankingDetail(event.weightClassId)
            }
        }
    }

    RankingScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    state: RankingUiState,
    onAction: (RankingUiAction) -> Unit
) {
    // 1. Theme & Resources
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current

    // 2. Compose Core States
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val snackbarHostState = remember { SnackbarHostState() }
    val mensListState = rememberLazyListState()
    val womensListState = rememberLazyListState()

    // 3. UI Data & Definitions
    val tabs = listOf(strings.tabMens, strings.tabWomens)
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val errorMessage = strings.mapError(state.error)

    SnackbarEffect(
        message = errorMessage,
        snackbarHostState = snackbarHostState,
        actionLabel = strings.retry,
        onAction = { onAction(RankingUiAction.OnRefresh) },
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = colors.pagerBackground,
        contentWindowInsets = WindowInsets(0),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    ErrorSnackbar(
                        snackbarData = snackbarData,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            )
        },
        topBar = {
            Column(
                modifier = Modifier.background(colors.eventsTopBar)
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = strings.rankingsTitle.uppercase(),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        titleContentColor = colors.textPrimary
                    ),
                    scrollBehavior = scrollBehavior
                )

                AppTabRow(
                    tabs = tabs,
                    selectedTabIndex = pagerState.currentPage,
                    onTabSelected = { index -> coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    containerColor = Color.Transparent
                )
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.pagerBackground),
            beyondViewportPageCount = 1
        ) { page ->
            when (page) {
                0 -> RankingContainer(
                    weightClasses = state.weightClasses.filter { !it.isWomens },
                    isRefreshing = state.isRefreshing,
                    onRefresh = { onAction(RankingUiAction.OnRefresh) },
                    onWeightClassClicked = { weightClassId -> onAction(RankingUiAction.OnWeightClassClicked(weightClassId)) },
                    listState = mensListState,
                )
                1 -> RankingContainer(
                    weightClasses = state.weightClasses.filter { it.isWomens },
                    isRefreshing = state.isRefreshing,
                    onRefresh = { onAction(RankingUiAction.OnRefresh) },
                    onWeightClassClicked = { weightClassId -> onAction(RankingUiAction.OnWeightClassClicked(weightClassId)) },
                    listState = womensListState,
                )
            }
        }
    }
}
