package com.berkekucuk.mmaapp.presentation.screens.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
import com.berkekucuk.mmaapp.domain.model.AuthState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Apple
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithApple
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composeAuth
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.unit.dp
import com.berkekucuk.mmaapp.presentation.components.ErrorSnackbar
import com.berkekucuk.mmaapp.presentation.components.SnackbarEffect
import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.core.utils.AppErrorMapper
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreenRoot(
    onNavigateToProfile: (String) -> Unit,
    onNavigateToProfileEdit: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    viewModel: MenuViewModel = koinViewModel(),
    supabaseClient: SupabaseClient = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            when (event) {
                is MenuNavigationEvent.ToProfile -> onNavigateToProfile(event.userId)
                is MenuNavigationEvent.ToProfileEdit -> onNavigateToProfileEdit()
                is MenuNavigationEvent.ToSettings -> onNavigateToSettings()
                is MenuNavigationEvent.ToLeaderboard -> onNavigateToLeaderboard()
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val signInWithGoogleAction = supabaseClient.composeAuth.rememberSignInWithGoogle(
        onResult = { result ->
            when (result) {
                is NativeSignInResult.Success -> {}
                is NativeSignInResult.Error -> {
                    viewModel.onAction(MenuUiAction.OnErrorOccurred(AppError.SERVER_ERROR))
                }
                is NativeSignInResult.NetworkError -> {
                    viewModel.onAction(MenuUiAction.OnErrorOccurred(AppError.NETWORK))
                }
                is NativeSignInResult.ClosedByUser -> {}
            }
        },
        fallback = {
            coroutineScope.launch(Dispatchers.Main) {
                try {
                    supabaseClient.auth.signInWith(Google)
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    viewModel.onAction(MenuUiAction.OnErrorOccurred(AppErrorMapper.map(e)))
                }
            }
        }
    )

    val signInWithAppleAction = supabaseClient.composeAuth.rememberSignInWithApple(
        onResult = { result ->
            when (result) {
                is NativeSignInResult.Success -> {}
                is NativeSignInResult.Error -> {
                    viewModel.onAction(MenuUiAction.OnErrorOccurred(AppError.SERVER_ERROR))
                }
                is NativeSignInResult.NetworkError -> {
                    viewModel.onAction(MenuUiAction.OnErrorOccurred(AppError.NETWORK))
                }
                is NativeSignInResult.ClosedByUser -> {}
            }
        },
        fallback = {
            coroutineScope.launch(Dispatchers.Main) {
                try {
                    supabaseClient.auth.signInWith(Apple)
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    viewModel.onAction(MenuUiAction.OnErrorOccurred(AppErrorMapper.map(e)))
                }
            }
        }
    )

    val onStartGoogleSignIn = remember(signInWithGoogleAction) {
        {
            try {
                signInWithGoogleAction.startFlow()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                viewModel.onAction(MenuUiAction.OnErrorOccurred(AppErrorMapper.map(e)))
            }
        }
    }

    val onStartAppleSignIn = remember(signInWithAppleAction) {
        {
            try {
                signInWithAppleAction.startFlow()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                viewModel.onAction(MenuUiAction.OnErrorOccurred(AppErrorMapper.map(e)))
            }
        }
    }

    MenuScreen(
        state = state,
        onAction = viewModel::onAction,
        onStartGoogleSignIn = onStartGoogleSignIn,
        onStartAppleSignIn = onStartAppleSignIn
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
    // 1. Theme & Resources
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current

    // 2. Compose Core States
    val coroutineScope = rememberCoroutineScope()
    val showSignInSheet = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // 3. UI Data & Definitions
    val errorMessage = strings.mapError(state.error)

    SnackbarEffect(
        message = errorMessage,
        snackbarHostState = snackbarHostState,
        duration = SnackbarDuration.Short,
        onDismiss = { onAction(MenuUiAction.OnErrorShown) },
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
            TopAppBar(
                title = {
                    Text(
                        text = strings.menuTitle,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.topBarBackground,
                    titleContentColor = colors.textPrimary,
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

            HorizontalDivider(color = colors.dividerColor)
            MenuItemRow(
                icon = Icons.Filled.Person,
                title = strings.menuItemLeaderboard,
                onClick = { onAction(MenuUiAction.OnLeaderboardClicked) }
            )

            HorizontalDivider(color = colors.dividerColor)
            MenuItemRow(
                icon = Icons.Filled.Settings,
                title = strings.menuItemSettings,
                onClick = { onAction(MenuUiAction.OnSettingsClicked) }
            )
            HorizontalDivider(color = colors.dividerColor)

            Spacer(modifier = Modifier.weight(1f))

            if (state.authState is AuthState.Authenticated) {
                HorizontalDivider(color = colors.dividerColor)
                MenuItemRow(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = strings.profileSignOut,
                    tint = colors.loseColor,
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