package com.berkekucuk.mmaapp.presentation.screens.fight_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.core.utils.NotificationPermissionHandler
import com.berkekucuk.mmaapp.core.utils.OnResumeEffect
import com.berkekucuk.mmaapp.presentation.components.ErrorSnackbar
import com.berkekucuk.mmaapp.presentation.components.SnackbarEffect
import com.berkekucuk.mmaapp.presentation.components.AppTabRow
import com.berkekucuk.mmaapp.presentation.components.AppAlertDialog
import com.berkekucuk.mmaapp.presentation.components.FightItem
import com.berkekucuk.mmaapp.presentation.components.ListContainer
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FightDetailScreenRoot(
    viewModel: FightDetailViewModel = koinViewModel(),
    onNavigateToFighterDetail: (fighterId: String) -> Unit,
    onNavigateToEventDetail: (eventId: String) -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val showPermissionRequest = remember { mutableStateOf(false) }
    val showSettingsDialog = remember { mutableStateOf(false) }
    val strings = LocalAppStrings.current

    NotificationPermissionHandler(
        trigger = showPermissionRequest.value,
        onResult = { isGranted ->
            showPermissionRequest.value = false
            if (isGranted) {
                viewModel.onAction(FightDetailUiAction.OnNotificationClicked)
            }
        },
        onDismiss = { showPermissionRequest.value = false }
    )

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            when (event) {
                is FightDetailNavigationEvent.ToFighterDetail -> onNavigateToFighterDetail(event.fighterId)
                is FightDetailNavigationEvent.Back -> onNavigateBack()
                is FightDetailNavigationEvent.ToEventDetail -> onNavigateToEventDetail(event.eventId)
                is FightDetailNavigationEvent.ToLeaderboard -> onNavigateToLeaderboard()
                is FightDetailNavigationEvent.RequestNotificationPermission -> {
                    showPermissionRequest.value = true
                }
                is FightDetailNavigationEvent.ShowSettingsDialog -> {
                    showSettingsDialog.value = true
                }
            }
        }
    }

    if (showSettingsDialog.value) {
        AppAlertDialog(
            onDismissRequest = { showSettingsDialog.value = false },
            onConfirmClick = {
                showSettingsDialog.value = false
                viewModel.onAction(FightDetailUiAction.OnOpenSettingsClicked)
            },
            title = strings.notificationPermissionSettingsTitle,
            text = strings.notificationPermissionSettingsMessage,
            confirmText = strings.dialogAccept,
            dismissText = strings.dialogCancel
        )
    }

    FightDetailScreen(
        state = uiState,
        fromEventDetail = viewModel.fromEventDetail,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FightDetailScreen(
    state: FightDetailUiState,
    fromEventDetail: Boolean,
    onAction: (FightDetailUiAction) -> Unit,
) {
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current
    val eventId = state.fight?.eventId
    val displayTitle = state.fight?.eventName
    val fight = state.fight
    val hasMetaInfo = fight != null && (
            fight.roundsFormat.isNotBlank() ||
            fight.roundSummary.isNotBlank() ||
                    fight.weightClassLbs != null
            )

    val tabs = listOf(strings.tabFightDetails, strings.tabFightComparison)
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val snackbarHostState = remember { SnackbarHostState() }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val navBarBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val coroutineScope = rememberCoroutineScope()

    val onErrorShown = remember(onAction) { { onAction(FightDetailUiAction.OnErrorShown) } }
    val onRefresh = remember(onAction) { { onAction(FightDetailUiAction.OnRefresh) } }
    val onBackClick = remember(onAction) { { onAction(FightDetailUiAction.OnBackClicked) } }
    val onRedCornerClick = remember(onAction, fight) {
        fight?.redCorner?.fighter?.fighterId?.let { id -> { onAction(FightDetailUiAction.OnFighterClicked(id)) } }
    }
    val onBlueCornerClick = remember(onAction, fight) {
        fight?.blueCorner?.fighter?.fighterId?.let { id -> { onAction(FightDetailUiAction.OnFighterClicked(id)) } }
    }
    val onEventLinkClick = remember(onAction, eventId, fromEventDetail) {
        {
            if (fromEventDetail) {
                onAction(FightDetailUiAction.OnBackClicked)
            } else if (!eventId.isNullOrBlank()) {
                onAction(FightDetailUiAction.OnEventClicked(eventId))
            }
        }
    }
    val onLeaderboardClick = remember(onAction) { { onAction(FightDetailUiAction.OnLeaderboardClicked) } }

    val showPredictionConfirmDialog = remember { mutableStateOf(false) }
    val pendingPredictionFighterId = remember { mutableStateOf<String?>(null) }
    val pendingPredictionFighterName = remember(pendingPredictionFighterId.value, fight) {
        fight?.participants?.find { it.fighter.fighterId == pendingPredictionFighterId.value }?.fighter?.name ?: ""
    }

    val onPredict = remember {
        { id: String ->
            pendingPredictionFighterId.value = id
            showPredictionConfirmDialog.value = true
        }
    }

    val onPredictionDialogDismiss = remember {
        {
            showPredictionConfirmDialog.value = false
            pendingPredictionFighterId.value = null
        }
    }

    val onPredictionConfirmed = remember(onAction) {
        {
            showPredictionConfirmDialog.value = false
            val id = pendingPredictionFighterId.value
            if (id != null) {
                onAction(FightDetailUiAction.OnSubmitPredictionClicked(id))
            }
            pendingPredictionFighterId.value = null
        }
    }

    val showNotificationDialog = remember { mutableStateOf(false) }
    val onNotificationDialogDismiss = remember { { showNotificationDialog.value = false } }
    val onNotificationConfirmed = remember(onAction) {
        {
            showNotificationDialog.value = false
            onAction(FightDetailUiAction.OnNotificationClicked)
        }
    }

    val isRetryableError = state.error == AppError.NETWORK
    val errorMessage = strings.mapError(state.error)

    OnResumeEffect { onAction(FightDetailUiAction.OnResume) }

    SnackbarEffect(
        message = errorMessage,
        snackbarHostState = snackbarHostState,
        duration = if (isRetryableError) SnackbarDuration.Indefinite else SnackbarDuration.Short,
        actionLabel = if (isRetryableError) strings.retry else null,
        onAction = if (isRetryableError) onRefresh else null,
        onDismiss = if (!isRetryableError) onErrorShown else null,
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = colors.pagerBackground,
        contentWindowInsets = WindowInsets.statusBars,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = navBarBottomPadding),
                snackbar = { snackbarData ->
                    ErrorSnackbar(
                        snackbarData = snackbarData,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            )
        },
        topBar = {
            Column(modifier = Modifier.background(colors.eventDetailTopBar)) {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = strings.contentDescriptionBack,
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showNotificationDialog.value = true }) {
                            Icon(
                                imageVector = if (state.isNotificationEnabled) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                                contentDescription = null,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        titleContentColor = colors.textPrimary,
                        navigationIconContentColor = colors.textPrimary,
                        actionIconContentColor = colors.textPrimary,
                    ),
                    scrollBehavior = scrollBehavior,
                )
                if (fight != null) {
                    FightItem(
                        fight = fight,
                        modifier = Modifier.height(108.dp),
                        backgroundColor = Color.Transparent,
                        onRedCornerClick = onRedCornerClick,
                        onBlueCornerClick = onBlueCornerClick,
                    )
                }
                AppTabRow(
                    tabs = tabs,
                    pagerState = pagerState,
                    coroutineScope = coroutineScope,
                    containerColor = Color.Transparent,
                )
            }
        },
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.pagerBackground),
            beyondViewportPageCount = 1,
        ) { page ->
            when (page) {
                0 -> ListContainer(
                    isRefreshing = state.isRefreshing,
                    onRefresh = onRefresh,
                    contentPadding = PaddingValues(top = 8.dp),
                    verticalSpacing = 8.dp,
                    extraBottomPadding = navBarBottomPadding,
                ) {
                    if (state.showPredictionBoard){
                        item(contentType = "PredictionBoard") {
                            PredictionBoard(
                                state = state,
                                onPredict = onPredict,
                                onLeaderboardClick = onLeaderboardClick
                            )
                        }
                    }
                    item(contentType = "FightDetailContainer") {
                        FightDetailContainer(
                            redCorner = fight?.redCorner,
                            blueCorner = fight?.blueCorner,
                            eventDate = state.fight?.eventDate,
                        )
                    }
                    if (hasMetaInfo) {
                        item(contentType = "FightMetaCard") {
                            FightMetaCard(fight = fight)
                        }
                    }
                    if (!eventId.isNullOrBlank() && displayTitle != null) {
                        item(contentType = "EventLink") {
                            EventLinkRow(
                                eventName = displayTitle,
                                isBackNavigation = fromEventDetail,
                                onClick = onEventLinkClick,
                            )
                        }
                    }
                }
                1 -> ListContainer(
                    isRefreshing = state.isRefreshing,
                    onRefresh = onRefresh,
                    contentPadding = PaddingValues(top = 8.dp),
                    verticalSpacing = 8.dp,
                    extraBottomPadding = navBarBottomPadding,
                ) {
                    if (fight != null) {
                        item(contentType = "RadarChart") {
                            FighterRadarChart(
                                redCorner = fight.redCorner,
                                blueCorner = fight.blueCorner,
                                redFighterFull = state.redFighter,
                                blueFighterFull = state.blueFighter,
                            )
                        }
                    }
                }
            }
        }
    }

    if (showNotificationDialog.value) {
        AppAlertDialog(
            onDismissRequest = onNotificationDialogDismiss,
            onConfirmClick = onNotificationConfirmed,
            text = if (state.isNotificationEnabled) strings.fightNotificationRemoveDialogMessage else strings.fightNotificationDialogMessage,
            confirmText = strings.dialogAccept,
            dismissText = strings.dialogCancel
        )
    }

    if (showPredictionConfirmDialog.value) {
        AppAlertDialog(
            onDismissRequest = onPredictionDialogDismiss,
            onConfirmClick = onPredictionConfirmed,
            title = strings.predictionConfirmTitle,
            text = strings.predictionConfirmMessage(pendingPredictionFighterName),
            confirmText = strings.dialogAccept,
            dismissText = strings.dialogCancel
        )
    }
}