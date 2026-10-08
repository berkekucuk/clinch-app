package com.berkekucuk.mmaapp.presentation.screens.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkekucuk.mmaapp.domain.model.AuthState
import com.berkekucuk.mmaapp.domain.repository.UserRepository
import com.berkekucuk.mmaapp.domain.repository.AuthRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.berkekucuk.mmaapp.domain.repository.NotificationRepository
import com.berkekucuk.mmaapp.domain.repository.PredictionRepository
import com.berkekucuk.mmaapp.core.utils.AppErrorMapper

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

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.authState
                .collectLatest { authState ->
                    val userId = (authState as? AuthState.Authenticated)?.userId
                    _state.update { it.copy(authState = authState, userId = userId) }

                    if (userId != null) {
                        coroutineScope {
                            launch { userRepository.syncUser(userId) }
                            launch { predictionRepository.syncPredictions(userId, limit = 20, offset = 0) }
                            launch { notificationRepository.syncFightNotifications(userId) }
                            launch {
                                userRepository.getUser(userId)
                                    .collect { user ->
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
                    } else {
                        _state.update {
                            it.copy(
                                avatarUrl = null,
                                name = null,
                                username = null
                            )
                        }
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
                        .onFailure { e ->
                            _state.update { it.copy(error = AppErrorMapper.map(e)) }
                        }
                }
            }
            MenuUiAction.OnLeaderboardClicked -> {
                navigateTo(MenuNavigationEvent.ToLeaderboard)
            }
            MenuUiAction.OnSnackbarDismissed -> {
                _state.update { it.copy(error = null, showSignInSuccess = false) }
            }
            MenuUiAction.OnSignInSuccess -> {
                _state.update { it.copy(showSignInSuccess = true) }
            }
        }
    }

    private fun navigateTo(event: MenuNavigationEvent) {
        viewModelScope.launch {
            _navigation.emit(event)
        }
    }
}
