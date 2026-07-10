package com.berkekucuk.mmaapp.presentation.screens.fight_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.Role
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
import com.berkekucuk.mmaapp.core.utils.NotificationPermissionHandler
import com.berkekucuk.mmaapp.presentation.components.ErrorSnackbar
import com.berkekucuk.mmaapp.presentation.components.SnackbarEffect
import com.berkekucuk.mmaapp.presentation.components.AppTabRow
import com.berkekucuk.mmaapp.presentation.components.AppAlertDialog
import com.berkekucuk.mmaapp.presentation.components.FightItem
import com.berkekucuk.mmaapp.core.utils.isIos
import com.berkekucuk.mmaapp.presentation.components.ListContainer
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.launch

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
                viewModel.onAction(FightDetailUiAction.OnNotificationClicked(false))
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
    // 1. Theme & Resources
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current

    // 2. Compose Core States
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val navBarBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val snackbarHostState = remember { SnackbarHostState() }

    // 3. UI Data & Definitions
    val eventId = state.fight?.eventId
    val displayTitle = state.fight?.eventName
    val fight = state.fight
    val hasMetaInfo = fight != null && (
            fight.roundsFormat.isNotBlank() || fight.roundSummary.isNotBlank() || fight.weightClassLbs != null
            )
    val tabs = listOf(strings.tabFightDetails, strings.tabFightComparison)
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val selectedRisk = remember { mutableStateOf(50) }
    val pendingPredictionFighterName = remember(state.pendingPredictionFighterId, fight) {
        fight?.participants?.find { it.fighter.fighterId == state.pendingPredictionFighterId }?.fighter?.name ?: ""
    }
    val errorMessage = strings.mapError(state.error)
    val selectedNotificationIsAlarm = remember { mutableStateOf(false) }

    // 4. UI Actions
    val onRedCornerClick = fight?.redCorner?.fighter?.fighterId?.let { id -> { onAction(FightDetailUiAction.OnFighterClicked(id)) } }
    val onBlueCornerClick = fight?.blueCorner?.fighter?.fighterId?.let { id -> { onAction(FightDetailUiAction.OnFighterClicked(id)) } }
    val onEventLinkClick = {
        if (fromEventDetail) {
            onAction(FightDetailUiAction.OnBackClicked)
        } else if (!eventId.isNullOrBlank()) {
            onAction(FightDetailUiAction.OnEventClicked(eventId))
        }
    }
    val onPredict = { id: String ->
        selectedRisk.value = 50
        onAction(FightDetailUiAction.OnPredictClicked(id))
    }
    val onPredictionConfirmed = {
        val id = state.pendingPredictionFighterId
        if (id != null) {
            onAction(FightDetailUiAction.OnSubmitPredictionClicked(id, selectedRisk.value))
        }
    }

    SnackbarEffect(
        message = errorMessage,
        snackbarHostState = snackbarHostState,
        duration = SnackbarDuration.Short,
        onDismiss = { onAction(FightDetailUiAction.OnErrorShown) },
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
                        IconButton(onClick = { onAction(FightDetailUiAction.OnBackClicked) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = strings.contentDescriptionBack,
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { onAction(FightDetailUiAction.OnNotificationIconClicked) }) {
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
                    selectedTabIndex = pagerState.currentPage,
                    onTabSelected = { index -> coroutineScope.launch { pagerState.animateScrollToPage(index) } },
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
                    onRefresh = { onAction(FightDetailUiAction.OnRefresh) },
                    contentPadding = PaddingValues(top = 8.dp),
                    verticalSpacing = 8.dp,
                    extraBottomPadding = navBarBottomPadding,
                ) {
                    if (state.showPredictionBoard){
                        item(contentType = "PredictionBoard") {
                            PredictionBoard(
                                state = state,
                                onPredict = onPredict,
                                onLeaderboardClick = { onAction(FightDetailUiAction.OnLeaderboardClicked) }
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
                                onClick = onEventLinkClick
                            )
                        }
                    }
                }
                1 -> ListContainer(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { onAction(FightDetailUiAction.OnRefresh) },
                    contentPadding = PaddingValues(top = 8.dp),
                    verticalSpacing = 8.dp,
                    extraBottomPadding = navBarBottomPadding,
                ) {
                    if (fight != null) {
                        item(contentType = "RadarChart") {
                            FighterRadarChart(
                                redCorner = fight.redCorner,
                                blueCorner = fight.blueCorner,
                            )
                        }
                    }
                }
            }
        }
    }

    if (state.showNotificationDialog) {
        if (!state.isNotificationEnabled && !isIos) {
            AppAlertDialog(
                onDismissRequest = { onAction(FightDetailUiAction.OnDismissNotificationDialog) },
                onConfirmClick = { onAction(FightDetailUiAction.OnNotificationClicked(selectedNotificationIsAlarm.value)) },
                title = strings.notificationTypeTitle,
                confirmText = strings.dialogAccept,
                dismissText = strings.dialogCancel,
                isConfirmLoading = state.isSubmittingNotification,
                content = {
                    Column(modifier = Modifier.selectableGroup()) {
                        Text(
                            text = strings.notificationTypeMessage,
                            color = colors.textSecondary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        val options = listOf(
                            false to strings.notificationTypeRegular,
                            true to strings.notificationTypeAlarm
                        )
                        options.forEach { (isAlarm, label) ->
                            val selected = (selectedNotificationIsAlarm.value == isAlarm)
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .background(
                                        color = if (selected) colors.winnerFrame.copy(alpha = 0.1f) else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (selected) colors.winnerFrame else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .selectable(
                                        selected = selected,
                                        onClick = { selectedNotificationIsAlarm.value = isAlarm },
                                        role = Role.RadioButton
                                    )
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selected,
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = colors.winnerFrame,
                                        unselectedColor = colors.textSecondary
                                    )
                                )
                                Text(
                                    text = label,
                                    color = colors.textPrimary,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }
                }
            )
        } else {
            AppAlertDialog(
                onDismissRequest = { onAction(FightDetailUiAction.OnDismissNotificationDialog) },
                onConfirmClick = { onAction(FightDetailUiAction.OnNotificationClicked(selectedNotificationIsAlarm.value)) },
                text = if (state.isNotificationEnabled) {
                    strings.fightReminderRemoveDialogMessage
                } else {
                    strings.fightReminderDialogMessage
                },
                confirmText = strings.dialogAccept,
                dismissText = strings.dialogCancel,
                isConfirmLoading = state.isSubmittingNotification
            )
        }
    }

    if (state.showPredictionConfirmDialog) {
        AppAlertDialog(
            onDismissRequest = { onAction(FightDetailUiAction.OnDismissPredictionDialog) },
            onConfirmClick = onPredictionConfirmed,
            title = strings.predictionConfirmTitle,
            confirmText = strings.dialogAccept,
            dismissText = strings.dialogCancel,
            isConfirmLoading = state.isSubmittingPrediction,
            content = {
                Column(modifier = Modifier.selectableGroup()) {
                    Text(
                        text = strings.predictionConfirmMessage(pendingPredictionFighterName),
                        color = colors.textSecondary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    val options = listOf(
                        25 to strings.riskUnsure,
                        50 to strings.riskNormal,
                        75 to strings.riskConfident,
                        100 to strings.riskAllIn
                    )
                    options.forEach { (value, label) ->
                        val selected = (selectedRisk.value == value)
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(
                                    color = if (selected) colors.winnerFrame.copy(alpha = 0.1f) else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (selected) colors.winnerFrame else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .selectable(
                                    selected = selected,
                                    onClick = { selectedRisk.value = value },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selected,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = colors.winnerFrame,
                                    unselectedColor = colors.textSecondary
                                )
                            )
                            Text(
                                text = label,
                                color = colors.textPrimary,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}