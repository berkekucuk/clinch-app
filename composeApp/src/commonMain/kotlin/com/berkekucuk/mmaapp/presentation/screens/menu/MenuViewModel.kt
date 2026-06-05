package com.berkekucuk.mmaapp.presentation.screens.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkekucuk.mmaapp.domain.model.AuthState
import com.berkekucuk.mmaapp.domain.repository.UserRepository
import com.berkekucuk.mmaapp.domain.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.berkekucuk.mmaapp.domain.repository.NotificationRepository
import com.berkekucuk.mmaapp.domain.repository.PredictionRepository

class MenuViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val predictionRepository: PredictionRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()
    private val _navigation = MutableSharedFlow<MenuNavigationEvent>()
    val navigation = _navigation.asSharedFlow()

    init {
        observeAuthState()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.authState
                .flatMapLatest { authState ->
                    val userId = (authState as? AuthState.Authenticated)?.userId

                    // 1. First, reflect the Auth state to the UI state
                    _state.update { it.copy(authState = authState, userId = userId) }

                    if (userId != null) {
                        // 2. If user exists, start the sync operations (run in background)
                        launch { userRepository.syncUser(userId) }
                        launch { predictionRepository.syncPredictions(userId, limit = 20, offset = 0) }
                        launch { notificationRepository.syncFightNotifications(userId) }

                        // 3. Return the User Flow (flatMapLatest will start collecting it)
                        userRepository.getUser(userId)
                    } else {
                        // 4. If there is no user, return a flow emitting null and clear the state
                        _state.update { it.copy(avatarUrl = null, name = null, username = null) }
                        flowOf(null)
                    }
                }
                .collect { user ->
                    // 5. Update the UI whenever user data changes (from Local DB)
                    _state.update { state ->
                        state.copy(
                            avatarUrl = user?.avatarUrl,
                            name = user?.fullName,
                            username = user?.username
                        )
                    }
                }
        }
    }

    fun onAction(action: MenuUiAction) {
        when (action) {
            MenuUiAction.OnProfileClicked -> {
                _state.value.userId?.let { userId ->
                    navigateTo(MenuNavigationEvent.ToProfile(userId))
                }
            }
            MenuUiAction.OnProfileEditClicked -> {
                    navigateTo(MenuNavigationEvent.ToProfileEdit)
            }

            MenuUiAction.OnSettingsClicked -> {
                navigateTo(MenuNavigationEvent.ToSettings)
            }
            MenuUiAction.OnSignOutClicked -> {
                viewModelScope.launch {
                    authRepository.signOut()
                }
            }
            MenuUiAction.OnLeaderboardClicked -> {
                navigateTo(MenuNavigationEvent.ToLeaderboard)
            }
            is MenuUiAction.OnErrorOccurred -> {
                _state.update { it.copy(error = action.error) }
            }
            MenuUiAction.OnErrorShown -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun navigateTo(event: MenuNavigationEvent) {
        viewModelScope.launch {
            _navigation.emit(event)
        }
    }
}
