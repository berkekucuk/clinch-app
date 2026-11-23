package com.berkekucuk.mmaapp.presentation.screens.menu

import com.berkekucuk.mmaapp.presentation.components.AppAlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import org.jetbrains.compose.resources.vectorResource
import mmaapp.composeapp.generated.resources.ic_x_logo
import mmaapp.composeapp.generated.resources.Res
import com.berkekucuk.mmaapp.core.utils.X_USERNAME
import com.berkekucuk.mmaapp.core.utils.SUPPORT_EMAIL
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.RateReview
import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
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
import com.berkekucuk.mmaapp.core.utils.openStoreReview
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
    // Theme & Resources
    val uriHandler = LocalUriHandler.current

    // Compose Core States
    val coroutineScope = rememberCoroutineScope()
    val showSignInSheet = remember { mutableStateOf(false) }
    val showSignOutDialog = remember { mutableStateOf(false) }
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
                actions = {
                    if (state.authState is AuthState.Authenticated) {
                        IconButton(onClick = { showSignOutDialog.value = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = AppTheme.strings.profileSignOut,
                                tint = AppTheme.colors.loseColor
                            )
                        }
                    }
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
            if ((state.authState as? AuthState.Authenticated)?.role == UserRole.ADMIN) {
                MenuItemRow(
                    icon = Icons.Filled.Notifications,
                    title = "Send Notification",
                    onClick = { onAction(MenuUiAction.OnSendNotificationClicked) }
                )
                HorizontalDivider(color = AppTheme.colors.dividerColor)
            }
            MenuItemRow(
                icon = Icons.Filled.RateReview,
                title = AppTheme.strings.menuItemRateApp,
                onClick = { openStoreReview() }
            )
            HorizontalDivider(color = AppTheme.colors.dividerColor)
            MenuItemRow(
                icon = vectorResource(Res.drawable.ic_x_logo),
                title = AppTheme.strings.menuItemFollowUs,
                subtitle = "@$X_USERNAME",
                onClick = {
                    try {
                        uriHandler.openUri("https://x.com/$X_USERNAME")
                    } catch (e: Exception) {
                        // ignore
                    }
                }
            )
            HorizontalDivider(color = AppTheme.colors.dividerColor)
            MenuItemRow(
                icon = Icons.Filled.Email,
                title = AppTheme.strings.menuItemContact,
                subtitle = SUPPORT_EMAIL,
                onClick = {
                    try {
                        uriHandler.openUri("mailto:$SUPPORT_EMAIL")
                    } catch (e: Exception) {
                        // ignore
                    }
                }
            )
            HorizontalDivider(color = AppTheme.colors.dividerColor)
            MenuItemRow(
                icon = Icons.Filled.Settings,
                title = AppTheme.strings.menuItemSettings,
                onClick = { onAction(MenuUiAction.OnSettingsClicked) }
            )
            HorizontalDivider(color = AppTheme.colors.dividerColor)
        }
    }

    if (showSignOutDialog.value) {
        AppAlertDialog(
            onDismissRequest = { showSignOutDialog.value = false },
            onConfirmClick = {
                showSignOutDialog.value = false
                onAction(MenuUiAction.OnSignOutClicked)
            },
            onDismissClick = { showSignOutDialog.value = false },
            title = AppTheme.strings.profileSignOut,
            text = AppTheme.strings.profileSignOutConfirm,
            confirmText = AppTheme.strings.profileSignOut,
            dismissText = AppTheme.strings.commonCancel,
            isDestructive = true
        )
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