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
import kotlinx.coroutines.launch

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
    // 1. Theme & Resources
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current
    val appLogoRes = if (colors.isDark) Res.drawable.app_logo else Res.drawable.app_logo_light

    // 2. Compose Core States
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val snackbarHostState = remember { SnackbarHostState() }
    val upcomingListState = rememberLazyListState()
    val completedListState = rememberLazyListState()

    // 3. UI Data & Definitions
    val tabs = listOf(strings.tabUpcoming, strings.tabCompleted)
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val errorMessage = strings.mapError(state.error)

    // 4. Update Dialog Logic
    val isForceUpdate = state.updateStatus is AppUpdateStatus.ForceUpdate
    val showUpdateDialog = state.updateStatus != null && state.updateStatus != AppUpdateStatus.UpToDate
    val updateDialogTitle = if (isForceUpdate) strings.updateForceTitle else strings.updateFlexibleTitle
    val updateDialogText = if (isForceUpdate) strings.updateForceMessage else strings.updateFlexibleMessage
    val updateDialogDismissText = if (!isForceUpdate) strings.updateLaterButton else null
    val updateDialogDismissAction: (() -> Unit)? = if (!isForceUpdate) { { onAction(HomeUiAction.OnDismissFlexibleUpdate) } } else null

    SnackbarEffect(
        message = errorMessage,
        snackbarHostState = snackbarHostState,
        actionLabel = strings.retry,
        onAction = { onAction(HomeUiAction.OnRefreshCompletedTab) },
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
                                painter = painterResource(appLogoRes),
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
                        IconButton(onClick = { onAction(HomeUiAction.OnSearchClicked) }) {
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
                    selectedTabIndex = pagerState.currentPage,
                    onTabSelected = { index -> coroutineScope.launch { pagerState.animateScrollToPage(index) } },
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
                        onRefresh = { onAction(HomeUiAction.OnRefreshUpcomingTab) },
                        onEventClick = { eventId -> onAction(HomeUiAction.OnEventClicked(eventId)) },
                        listState = upcomingListState,
                    )
                    1 -> CompletedContainer(
                        completedEvents = state.completedEvents,
                        isRefreshing = state.isRefreshingCompletedTab,
                        onRefresh = { onAction(HomeUiAction.OnRefreshCompletedTab) },
                        onEventClick = { eventId -> onAction(HomeUiAction.OnEventClicked(eventId)) },
                        availableYears = state.availableYears,
                        selectedYear = state.selectedYear,
                        onYearSelected = { year -> onAction(HomeUiAction.OnYearSelected(year)) },
                        listState = completedListState,
                    )
                }
            }
        }
    }

    if (showUpdateDialog) {
        AppAlertDialog(
            onDismissRequest = { updateDialogDismissAction?.invoke() },
            onConfirmClick = { openStore() },
            onDismissClick = updateDialogDismissAction,
            title = updateDialogTitle,
            text = updateDialogText,
            confirmText = strings.updateButton,
            dismissText = updateDialogDismissText
        )
    }
}
