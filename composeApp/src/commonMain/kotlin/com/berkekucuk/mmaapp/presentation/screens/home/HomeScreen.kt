package com.berkekucuk.mmaapp.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.berkekucuk.mmaapp.core.presentation.AppFonts
import com.berkekucuk.mmaapp.presentation.components.ErrorSnackbar
import com.berkekucuk.mmaapp.presentation.components.AppTabRow
import com.berkekucuk.mmaapp.presentation.components.SnackbarEffect
import com.berkekucuk.mmaapp.presentation.components.LoadingContent
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
import mmaapp.composeapp.generated.resources.Res
import mmaapp.composeapp.generated.resources.app_logo
import mmaapp.composeapp.generated.resources.app_logo_light
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import com.berkekucuk.mmaapp.core.utils.openStore
import com.berkekucuk.mmaapp.domain.model.AppUpdateStatus
import com.berkekucuk.mmaapp.presentation.components.AppAlertDialog

@Composable
fun HomeScreenRoot(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToEventDetail: (String) -> Unit,
    onNavigateToFighterSearch: () -> Unit,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            when (event) {
                is HomeNavigationEvent.ToEventDetail -> onNavigateToEventDetail(event.eventId)
                is HomeNavigationEvent.ToFighterSearch -> onNavigateToFighterSearch()
            }
        }
    }

    HomeScreen(
        state = uiState,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    onAction: (HomeUiAction) -> Unit,
) {
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current
    val tabs = listOf(strings.tabUpcoming, strings.tabCompleted)
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val snackbarHostState = remember { SnackbarHostState() }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val coroutineScope = rememberCoroutineScope()
    val upcomingListState = rememberLazyListState()
    val completedListState = rememberLazyListState()

    val onSearchClicked = remember(onAction) { { onAction(HomeUiAction.OnSearchClicked) } }
    val onRefreshUpcomingTab = remember(onAction) { { onAction(HomeUiAction.OnRefreshUpcomingTab) } }
    val onRefreshCompletedTab = remember(onAction) { { onAction(HomeUiAction.OnRefreshCompletedTab) } }
    val onEventClicked = remember(onAction) { { eventId: String -> onAction(HomeUiAction.OnEventClicked(eventId)) } }
    val onYearSelected = remember(onAction) { { year: Int -> onAction(HomeUiAction.OnYearSelected(year)) } }

    val errorMessage = strings.mapError(state.error)

    val updateStatus = state.updateStatus
    val isForceUpdate = updateStatus is AppUpdateStatus.ForceUpdate
    val onDismissFlexibleUpdate = remember(onAction) { { onAction(HomeUiAction.OnDismissFlexibleUpdate) } }

    SnackbarEffect(
        message = errorMessage,
        snackbarHostState = snackbarHostState,
        actionLabel = strings.retry,
        onAction = onRefreshCompletedTab,
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
                        Row(verticalAlignment = Alignment.CenterVertically){
                            Image(
                                painter = painterResource(
                                    if (colors.isDark) Res.drawable.app_logo
                                    else Res.drawable.app_logo_light
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "LINCH",
                                modifier = Modifier.offset(x = (-4).dp),
                                fontFamily = AppFonts.Montserrat,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onSearchClicked) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = colors.textPrimary,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        titleContentColor = colors.textPrimary,
                    ),
                    scrollBehavior = scrollBehavior
                )

                AppTabRow(
                    tabs = tabs,
                    pagerState = pagerState,
                    coroutineScope = coroutineScope,
                    containerColor = Color.Transparent
                )
            }
        }
    ) { innerPadding ->
        LoadingContent(
            isLoading = state.isLoading,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.pagerBackground),
                beyondViewportPageCount = 1
            ) { page ->
                when (page) {
                    0 -> UpcomingContainer(
                        events = state.upcomingEvents,
                        isRefreshing = state.isRefreshingUpcomingTab,
                        onRefresh = onRefreshUpcomingTab,
                        onEventClick = onEventClicked,
                        listState = upcomingListState,
                    )
                    1 -> CompletedContainer(
                        completedEvents = state.completedEvents,
                        isRefreshing = state.isRefreshingCompletedTab,
                        onRefresh = onRefreshCompletedTab,
                        onEventClick = onEventClicked,
                        availableYears = state.availableYears,
                        selectedYear = state.selectedYear,
                        onYearSelected = onYearSelected,
                        listState = completedListState,
                    )
                }
            }
        }
    }

    if (updateStatus != null && updateStatus != AppUpdateStatus.UpToDate) {
        AppAlertDialog(
            onDismissRequest = { if (!isForceUpdate) onDismissFlexibleUpdate() },
            onConfirmClick = { openStore() },
            onDismissClick = if (!isForceUpdate) onDismissFlexibleUpdate else null,
            title = if (isForceUpdate) strings.updateForceTitle else strings.updateFlexibleTitle,
            text = if (isForceUpdate) strings.updateForceMessage else strings.updateFlexibleMessage,
            confirmText = strings.updateButton,
            dismissText = if (!isForceUpdate) strings.updateLaterButton else null
        )
    }
}
