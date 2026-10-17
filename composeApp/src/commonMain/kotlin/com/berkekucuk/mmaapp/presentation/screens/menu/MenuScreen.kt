package com.berkekucuk.mmaapp.presentation.screens.menu

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.berkekucuk.mmaapp.domain.enums.UserRole
import com.berkekucuk.mmaapp.domain.model.AuthState
import com.berkekucuk.mmaapp.presentation.components.AppSnackbar
import com.berkekucuk.mmaapp.presentation.components.AppSnackbarEffect
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun MenuScreenRoot(
    onNavigateToProfile: (String) -> Unit,
    onNavigateToProfileEdit: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToSendNotification: () -> Unit,
    viewModel: MenuViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            when (event) {
                is MenuNavigationEvent.ToProfile -> onNavigateToProfile(event.userId)
                is MenuNavigationEvent.ToProfileEdit -> onNavigateToProfileEdit()
                is MenuNavigationEvent.ToSettings -> onNavigateToSettings()
                is MenuNavigationEvent.ToLeaderboard -> onNavigateToLeaderboard()
                is MenuNavigationEvent.ToSendNotification -> onNavigateToSendNotification()
            }
        }
    }

    val authHandler = rememberSocialAuthHandler(
        onSuccess = {
            viewModel.onAction(MenuUiAction.OnSignInSuccess)
        }
    )

    MenuScreen(
        state = state,
        onAction = viewModel::onAction,
        onStartGoogleSignIn = authHandler.startGoogleSignIn,
        onStartAppleSignIn = authHandler.startAppleSignIn
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    state: MenuUiState,
    onAction: (MenuUiAction) -> Unit,
    onStartGoogleSignIn: () -> Unit,
    onStartAppleSignIn: () -> Unit,
) {
    // Compose Core States
    val coroutineScope = rememberCoroutineScope()
    val showSignInSheet = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val isSnackbarSuccess = remember { mutableStateOf(false) }

    // UI Data & Definitions
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

    AppSnackbarEffect(
        message = snackbarMessage,
        snackbarHostState = snackbarHostState,
        duration = SnackbarDuration.Short,
        onDismiss = { onAction(MenuUiAction.OnSnackbarDismissed) },
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppTheme.colors.pagerBackground,
        contentWindowInsets = WindowInsets(0),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
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
            TopAppBar(
                title = {
                    Text(
                        text = AppTheme.strings.menuTitle,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.topBarBackground,
                    titleContentColor = AppTheme.colors.textPrimary,
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (state.authState) {
                is AuthState.Loading -> {}

                is AuthState.Unauthenticated -> {
                    UnauthenticatedTopHeader(onSignInClick = { showSignInSheet.value = true })
                }

                is AuthState.Authenticated -> {
                    AuthenticatedSection(
                        name = state.name ?: "",
                        username = state.username,
                        avatarUrl = state.avatarUrl,
                        onProfileClick = { onAction(MenuUiAction.OnProfileClicked) },
                        onProfileEditClick = { onAction(MenuUiAction.OnProfileEditClicked) },
                    )
                }
            }

            HorizontalDivider(color = AppTheme.colors.dividerColor)
            MenuItemRow(
                icon = Icons.Filled.Leaderboard,
                title = AppTheme.strings.menuItemLeaderboard,
                onClick = { onAction(MenuUiAction.OnLeaderboardClicked) }
            )

            HorizontalDivider(color = AppTheme.colors.dividerColor)
            if ((state.authState as? AuthState.Authenticated)?.role == UserRole.ADMIN) {
                MenuItemRow(
                    icon = Icons.Filled.Notifications,
                    title = "Send Notification",
                    onClick = { onAction(MenuUiAction.OnSendNotificationClicked) }
                )
                HorizontalDivider(color = AppTheme.colors.dividerColor)
            }
            MenuItemRow(
                icon = Icons.Filled.Settings,
                title = AppTheme.strings.menuItemSettings,
                onClick = { onAction(MenuUiAction.OnSettingsClicked) }
            )
            HorizontalDivider(color = AppTheme.colors.dividerColor)

            Spacer(modifier = Modifier.weight(1f))

            if (state.authState is AuthState.Authenticated) {
                HorizontalDivider(color = AppTheme.colors.dividerColor)
                MenuItemRow(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = AppTheme.strings.profileSignOut,
                    tint = AppTheme.colors.loseColor,
                    onClick = { onAction(MenuUiAction.OnSignOutClicked) },
                )
            }
        }
    }

    if (showSignInSheet.value) {
        SignInBottomSheet(
            onDismiss = { showSignInSheet.value = false },
            onStartGoogleSignIn = {
                showSignInSheet.value = false
                coroutineScope.launch {
                    kotlinx.coroutines.delay(300.milliseconds)
                    onStartGoogleSignIn()
                }
            },
            onStartAppleSignIn = {
                showSignInSheet.value = false
                onStartAppleSignIn()
            }
        )
    }
}