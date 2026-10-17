package com.berkekucuk.mmaapp.presentation.screens.leaderboard

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.berkekucuk.mmaapp.presentation.components.AppSnackbar
import com.berkekucuk.mmaapp.presentation.components.AppSnackbarEffect
import com.berkekucuk.mmaapp.presentation.components.AppTabRow
import com.berkekucuk.mmaapp.presentation.components.AppAlertDialog
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.launch

@Composable
fun LeaderboardScreenRoot(
    viewModel: LeaderboardViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToUserSearch: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            when (event) {
                is LeaderboardNavigationEvent.Back -> onNavigateBack()
                is LeaderboardNavigationEvent.ToUserProfile -> onNavigateToProfile(event.userId)
                LeaderboardNavigationEvent.ToUserSearch -> onNavigateToUserSearch()
            }
        }
    }

    LeaderboardScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    state: LeaderboardUiState,
    onAction: (LeaderboardUiAction) -> Unit
) {
    // Compose Core States
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val snackbarHostState = remember { SnackbarHostState() }
    val navBarBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val showInfoDialog = remember { mutableStateOf(false) }
    val weeklyListState = rememberLazyListState()
    val overallListState = rememberLazyListState()
    var overallLastScrolledPage by rememberSaveable { mutableStateOf(-1) }

    // UI Data & Definitions
    val tabs = listOf(AppTheme.strings.tabWeekly, AppTheme.strings.tabOverall)
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val errorMessage = AppTheme.strings.mapError(state.error)

    LaunchedEffect(state.overallLeaderboard) {
        if (overallLastScrolledPage != state.currentPage) {
            overallListState.scrollToItem(0)
            overallLastScrolledPage = state.currentPage
        }
    }

    AppSnackbarEffect(
        message = errorMessage,
        snackbarHostState = snackbarHostState,
        actionLabel = AppTheme.strings.retry,
        onAction = { onAction(LeaderboardUiAction.OnRefresh) },
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = AppTheme.colors.pagerBackground,
        contentWindowInsets = WindowInsets.statusBars,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = navBarBottomPadding),
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
                modifier = Modifier.background(AppTheme.colors.rankingTopBar)
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = AppTheme.strings.menuItemLeaderboard,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { onAction(LeaderboardUiAction.OnBackClicked) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = AppTheme.strings.contentDescriptionBack
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { onAction(LeaderboardUiAction.OnSearchClicked) }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                            )
                        }
                        IconButton(onClick = { showInfoDialog.value = true }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = AppTheme.strings.contentDescriptionInfo,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        navigationIconContentColor = AppTheme.colors.textPrimary,
                        titleContentColor = AppTheme.colors.textPrimary,
                        actionIconContentColor = AppTheme.colors.textPrimary,
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
                .background(AppTheme.colors.pagerBackground),
            beyondViewportPageCount = 1
        ) { page ->
            when (page) {
                0 -> LeaderboardContainer(
                    isOverall = false,
                    leaderboard = state.weeklyLeaderboard,
                    isRefreshing = state.isRefreshing,
                    currentPage = state.currentPage,
                    canGoNext = state.canGoNext,
                    currentUserId = state.currentUserId,
                    listState = weeklyListState,
                    onAction = onAction,
                    navBarBottomPadding = navBarBottomPadding
                )
                1 -> LeaderboardContainer(
                    isOverall = true,
                    leaderboard = state.overallLeaderboard,
                    isRefreshing = state.isRefreshing,
                    currentPage = state.currentPage,
                    canGoNext = state.canGoNext,
                    currentUserId = state.currentUserId,
                    listState = overallListState,
                    onAction = onAction,
                    navBarBottomPadding = navBarBottomPadding
                )
            }
        }
    }

    if (showInfoDialog.value) {
        AppAlertDialog(
            onDismissRequest = { showInfoDialog.value = false },
            onConfirmClick = { showInfoDialog.value = false },
            title = AppTheme.strings.leaderboardInfoTitle,
            text = state.infoText ?: "",
            confirmText = AppTheme.strings.leaderboardInfoClose
        )
    }
}