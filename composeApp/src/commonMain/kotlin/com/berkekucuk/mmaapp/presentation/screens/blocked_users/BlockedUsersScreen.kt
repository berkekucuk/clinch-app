package com.berkekucuk.mmaapp.presentation.screens.blocked_users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.berkekucuk.mmaapp.presentation.components.SnackbarEffect
import com.berkekucuk.mmaapp.presentation.screens.ranking_detail.RankedFighterRow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BlockedUsersScreenRoot(
    viewModel: BlockedUsersViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            when (event) {
                is BlockedUsersNavigationEvent.Back -> onNavigateBack()
                is BlockedUsersNavigationEvent.ToUserProfile -> onNavigateToProfile(event.userId)
            }
        }
    }

    BlockedUsersScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockedUsersScreen(
    state: BlockedUsersUiState,
    onAction: (BlockedUsersUiAction) -> Unit
) {
    // 1. Theme & Resources
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current

    // 2. Compose Core States
    val snackbarHostState = remember { SnackbarHostState() }
    val navBarBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    // 3. UI Data & Definitions
    val errorMessage = strings.mapError(state.error)

    SnackbarEffect(
        message = errorMessage,
        snackbarHostState = snackbarHostState,
        duration = SnackbarDuration.Short,
        onDismiss = { onAction(BlockedUsersUiAction.OnErrorShown) },
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
                            text = strings.settingsSectionBlockedUsers,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { onAction(BlockedUsersUiAction.OnBackClicked) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = strings.contentDescriptionBack
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        navigationIconContentColor = colors.textPrimary,
                        titleContentColor = colors.textPrimary,
                    )
                )
            }
        }
    ) { innerPadding ->
        ListContainer(
            isRefreshing = false,
            onRefresh = {},
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(top = 8.dp),
            verticalSpacing = 0.dp,
            extraBottomPadding = navBarBottomPadding,
        ) {
            if (state.blockedUsers.isEmpty()) {
                item(contentType = "EmptyState") {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.blockedUsersEmpty,
                            color = colors.textSecondary,
                            fontSize = 16.sp
                        )
                    }
                }
            } else {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.fightItemBackground)
                    ) {
                        state.blockedUsers.forEachIndexed { index, user ->
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
                                        TextButton(
                                            onClick = { onAction(BlockedUsersUiAction.OnUnblockClicked(user.id)) }
                                        ) {
                                            Text(
                                                text = strings.unblockUser,
                                                color = colors.winnerFrame,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                    },
                                    onFighterClicked = { onAction(BlockedUsersUiAction.OnUserClicked(user.id)) }
                                )

                                if (index < state.blockedUsers.lastIndex) {
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
}
