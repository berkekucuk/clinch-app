package com.berkekucuk.mmaapp.presentation.screens.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
import com.berkekucuk.mmaapp.presentation.components.ErrorSnackbar
import com.berkekucuk.mmaapp.presentation.components.ListContainer
import com.berkekucuk.mmaapp.presentation.components.LoadingContent
import com.berkekucuk.mmaapp.presentation.components.SnackbarEffect
import com.berkekucuk.mmaapp.presentation.components.AppAlertDialog
import com.berkekucuk.mmaapp.presentation.screens.ranking_detail.RankedFighterRow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LeaderboardScreenRoot(
    viewModel: LeaderboardViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            when (event) {
                is LeaderboardNavigationEvent.Back -> onNavigateBack()
                is LeaderboardNavigationEvent.ToUserProfile -> onNavigateToProfile(event.userId)
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
    val onBackClicked = remember(onAction) { { onAction(LeaderboardUiAction.OnBackClicked) } }
    val onUserClicked = remember(onAction) { { userId: String -> onAction(LeaderboardUiAction.OnUserClicked(userId)) } }
    val onRefresh = remember(onAction) { { onAction(LeaderboardUiAction.OnRefresh) } }
    val onErrorShown = remember(onAction) { { onAction(LeaderboardUiAction.OnErrorShown) } }
    val showInfoDialog = remember { mutableStateOf(false) }
    val onInfoDialogDismiss = remember { { showInfoDialog.value = false } }
    val onInfoDialogConfirmed = remember { { showInfoDialog.value = false} }
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current
    val snackbarHostState = remember { SnackbarHostState() }
    val navBarBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val errorMessage = strings.mapError(state.error)

    SnackbarEffect(
        message = errorMessage,
        snackbarHostState = snackbarHostState,
        duration = SnackbarDuration.Short,
        onDismiss = onErrorShown,
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
            Column(
                modifier = Modifier.background(colors.rankingTopBar)
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = strings.menuItemLeaderboard,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClicked) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = strings.contentDescriptionBack
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showInfoDialog.value = true }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = strings.contentDescriptionInfo,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        navigationIconContentColor = colors.textPrimary,
                        titleContentColor = colors.textPrimary,
                        actionIconContentColor = colors.textPrimary,
                    )
                )
            }
        }
    ) { innerPadding ->
        LoadingContent(
            isLoading = state.isLoading && state.leaderboard.isEmpty(),
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            ListContainer(
                isRefreshing = state.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp),
                verticalSpacing = 0.dp,
                extraBottomPadding = navBarBottomPadding,
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.fightItemBackground)
                    ) {
                        state.leaderboard.forEachIndexed { index, user ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                            ) {
                                RankedFighterRow(
                                    rankNumber = index + 1,
                                    name = user.fullName ?: user.username ?: "Unknown",
                                    record = user.username?.let { "@$it" } ?: "",
                                    imageUrl = user.avatarUrl ?: "",
                                    countryCode = null,
                                    trailingContent = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(end = 4.dp)
                                        ) {
                                            Text(
                                                text = user.totalPoints.toString(),
                                                color = colors.winnerFrame,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.EmojiEvents,
                                                contentDescription = null,
                                                tint = colors.winnerFrame,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    },
                                    onFighterClicked = { onUserClicked(user.id) }
                                )

                                if (index < state.leaderboard.lastIndex) {
                                    HorizontalDivider(
                                        color = colors.dividerColor,
                                        thickness = 0.8.dp,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showInfoDialog.value) {
        AppAlertDialog(
            onDismissRequest = onInfoDialogDismiss,
            onConfirmClick = onInfoDialogConfirmed,
            title = strings.leaderboardInfoTitle,
            text = state.infoText ?: "",
            confirmText = strings.leaderboardInfoClose
        )
    }
}
