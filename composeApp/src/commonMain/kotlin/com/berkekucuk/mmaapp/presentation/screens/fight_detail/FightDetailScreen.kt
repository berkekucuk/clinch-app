package com.berkekucuk.mmaapp.presentation.screens.fight_detail

import com.berkekucuk.mmaapp.core.presentation.AppTheme
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.berkekucuk.mmaapp.core.utils.NotificationPermissionHandler
import com.berkekucuk.mmaapp.core.utils.isIos
import com.berkekucuk.mmaapp.presentation.components.AppAlertDialog
import com.berkekucuk.mmaapp.presentation.components.AppTabRow
import com.berkekucuk.mmaapp.presentation.components.AppSnackbar
import com.berkekucuk.mmaapp.presentation.components.FightItem
import com.berkekucuk.mmaapp.presentation.components.ListContainer
import com.berkekucuk.mmaapp.presentation.components.AppSnackbarEffect
import com.berkekucuk.mmaapp.presentation.screens.menu.SignInBottomSheet
import com.berkekucuk.mmaapp.presentation.screens.menu.rememberSocialAuthHandler
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun FightDetailScreenRoot(
    viewModel: FightDetailViewModel = koinViewModel(),
    onNavigateToFighterDetail: (fighterId: String) -> Unit,
    onNavigateToEventDetail: (eventId: String) -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showPermissionRequest = remember { mutableStateOf(false) }

    NotificationPermissionHandler(
        trigger = showPermissionRequest.value,
        onResult = { isGranted ->
            showPermissionRequest.value = false
            if (isGranted) {
                viewModel.onAction(FightDetailUiAction.OnSubmitNotificationClicked(false))
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
            }
        }
    }

    val authHandler = rememberSocialAuthHandler(
        onSuccess = {
            viewModel.onAction(FightDetailUiAction.OnSignInSuccess)
        }
    )

    FightDetailScreen(
        state = state,
        fromEventDetail = viewModel.fromEventDetail,
        onAction = viewModel::onAction,
        onStartGoogleSignIn = authHandler.startGoogleSignIn,
        onStartAppleSignIn = authHandler.startAppleSignIn,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FightDetailScreen(
    state: FightDetailUiState,
    fromEventDetail: Boolean,
    onAction: (FightDetailUiAction) -> Unit,
    onStartGoogleSignIn: () -> Unit,
    onStartAppleSignIn: () -> Unit,
) {
    // Compose Core States
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val navBarBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val snackbarHostState = remember { SnackbarHostState() }
    val selectedRisk = remember { mutableStateOf(50) }
    val selectedNotificationIsAlarm = remember { mutableStateOf(false) }
    val isSnackbarSuccess = remember { mutableStateOf(false) }

    // UI Data & Definitions
    val eventId = state.fight?.eventId
    val displayTitle = state.fight?.eventName
    val fight = state.fight
    val hasMetaInfo = fight != null && (fight.roundsFormat.isNotBlank() || !fight.roundSummary.isNullOrBlank() || fight.weightClassLbs != null || !fight.referee.isNullOrBlank())
    val tabs = listOf(AppTheme.strings.tabFight, AppTheme.strings.tabFightCareer)
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val pendingPredictionFighterName = remember(state.pendingPredictionFighterId, fight) {
        fight?.participants?.find { it.fighter.fighterId == state.pendingPredictionFighterId }?.fighter?.name ?: ""
    }
    val errorMessage = AppTheme.strings.mapError(state.error)
    val snackbarMessage = when {
        state.error != null -> {
            isSnackbarSuccess.value = false
            errorMessage
        }
        state.showSignInSuccess -> {
            isSnackbarSuccess.value = true
            AppTheme.strings.signInSuccess
        }
        else -> null
    }

    // UI Actions
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

    AppSnackbarEffect(
        message = snackbarMessage,
        snackbarHostState = snackbarHostState,
        duration = SnackbarDuration.Short,
        onDismiss = { onAction(FightDetailUiAction.OnSnackbarDismissed) },
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
                        isSuccess = isSnackbarSuccess.value,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            )
        },
        topBar = {
            Column(modifier = Modifier.background(AppTheme.colors.eventDetailTopBar)) {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { onAction(FightDetailUiAction.OnBackClicked) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = AppTheme.strings.contentDescriptionBack,
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
                        titleContentColor = AppTheme.colors.textPrimary,
                        navigationIconContentColor = AppTheme.colors.textPrimary,
                        actionIconContentColor = AppTheme.colors.textPrimary,
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
                .background(AppTheme.colors.pagerBackground),
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
                    if (state.showPredictionBoard) {
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
                    if (fight != null && fight.stats.isNotEmpty()) {
                        item(contentType = "FightMatchStats") {
                            FightMatchStatsCard(fight = fight)
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
                        item(contentType = "FighterCareerStats") {
                            FighterCareerStatsSection(
                                redFighter = fight.redCorner?.fighter,
                                blueFighter = fight.blueCorner?.fighter,
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
                onConfirmClick = { onAction(FightDetailUiAction.OnSubmitNotificationClicked(selectedNotificationIsAlarm.value)) },
                title = AppTheme.strings.notificationTypeTitle,
                confirmText = AppTheme.strings.dialogAccept,
                dismissText = AppTheme.strings.dialogCancel,
                isConfirmLoading = state.isSubmittingNotification,
                content = {
                    Column(modifier = Modifier.selectableGroup()) {
                        Text(
                            text = AppTheme.strings.notificationTypeMessage,
                            color = AppTheme.colors.textSecondary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        val options = listOf(
                            false to AppTheme.strings.notificationTypeRegular,
                            true to AppTheme.strings.notificationTypeAlarm
                        )
                        options.forEach { (isAlarm, label) ->
                            val selected = (selectedNotificationIsAlarm.value == isAlarm)
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .background(
                                        color = if (selected) AppTheme.colors.winnerFrame.copy(alpha = 0.1f) else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (selected) AppTheme.colors.winnerFrame else Color.Transparent,
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
                                        selectedColor = AppTheme.colors.winnerFrame,
                                        unselectedColor = AppTheme.colors.textSecondary
                                    )
                                )
                                Text(
                                    text = label,
                                    color = AppTheme.colors.textPrimary,
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
                onConfirmClick = { onAction(FightDetailUiAction.OnSubmitNotificationClicked(selectedNotificationIsAlarm.value)) },
                title = if (state.isNotificationEnabled) {
                    AppTheme.strings.fightReminderRemoveDialogTitle
                } else {
                    AppTheme.strings.fightReminderDialogTitle
                },
                text = if (!state.isNotificationEnabled) AppTheme.strings.fightReminderDialogMessage else null,
                confirmText = AppTheme.strings.dialogAccept,
                dismissText = AppTheme.strings.dialogCancel,
                isConfirmLoading = state.isSubmittingNotification
            )
        }
    }

    if (state.showNotificationSettingsDialog) {
        AppAlertDialog(
            onDismissRequest = { onAction(FightDetailUiAction.OnDismissNotificationSettingsDialog) },
            onConfirmClick = { onAction(FightDetailUiAction.OnOpenNotificationSettingsClicked) },
            title = AppTheme.strings.notificationPermissionSettingsTitle,
            text = AppTheme.strings.notificationPermissionSettingsMessage,
            confirmText = AppTheme.strings.dialogAccept,
            dismissText = AppTheme.strings.dialogCancel
        )
    }

    if (state.showFullScreenIntentSettingsDialog) {
        AppAlertDialog(
            onDismissRequest = { onAction(FightDetailUiAction.OnDismissFullScreenIntentSettingsDialog) },
            onConfirmClick = { onAction(FightDetailUiAction.OnOpenFullScreenIntentSettingsClicked) },
            title = AppTheme.strings.fullScreenIntentSettingsTitle,
            text = AppTheme.strings.fullScreenIntentSettingsMessage,
            confirmText = AppTheme.strings.dialogAccept,
            dismissText = AppTheme.strings.dialogCancel
        )
    }

    if (state.showPredictionDialog) {
        AppAlertDialog(
            onDismissRequest = { onAction(FightDetailUiAction.OnDismissPredictionDialog) },
            onConfirmClick = onPredictionConfirmed,
            title = AppTheme.strings.predictionConfirmTitle,
            confirmText = AppTheme.strings.dialogAccept,
            dismissText = AppTheme.strings.dialogCancel,
            isConfirmLoading = state.isSubmittingPrediction,
            content = {
                Column(modifier = Modifier.selectableGroup()) {
                    Text(
                        text = AppTheme.strings.predictionConfirmMessage(pendingPredictionFighterName),
                        color = AppTheme.colors.textSecondary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    val options = listOf(
                        25 to AppTheme.strings.riskUnsure,
                        50 to AppTheme.strings.riskNormal,
                        75 to AppTheme.strings.riskConfident,
                        100 to AppTheme.strings.riskAllIn
                    )
                    options.forEach { (value, label) ->
                        val selected = (selectedRisk.value == value)
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(
                                    color = if (selected) AppTheme.colors.winnerFrame.copy(alpha = 0.1f) else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (selected) AppTheme.colors.winnerFrame else Color.Transparent,
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
                                    selectedColor = AppTheme.colors.winnerFrame,
                                    unselectedColor = AppTheme.colors.textSecondary
                                )
                            )
                            Text(
                                text = label,
                                color = AppTheme.colors.textPrimary,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
        )
    }

    if (state.showSignInSheet) {
        SignInBottomSheet(
            onDismiss = { onAction(FightDetailUiAction.OnDismissSignInSheet) },
            onStartGoogleSignIn = {
                onAction(FightDetailUiAction.OnDismissSignInSheet)
                coroutineScope.launch {
                    kotlinx.coroutines.delay(300.milliseconds)
                    onStartGoogleSignIn()
                }
            },
            onStartAppleSignIn = {
                onAction(FightDetailUiAction.OnDismissSignInSheet)
                onStartAppleSignIn()
            }
        )
    }
}