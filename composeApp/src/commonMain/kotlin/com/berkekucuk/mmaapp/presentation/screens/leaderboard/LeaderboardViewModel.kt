package com.berkekucuk.mmaapp.presentation.screens.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkekucuk.mmaapp.core.storage.LanguageStorage
import com.berkekucuk.mmaapp.core.utils.AppErrorMapper
import com.berkekucuk.mmaapp.domain.repository.AppConfigRepository
import com.berkekucuk.mmaapp.domain.repository.AuthRepository
import com.berkekucuk.mmaapp.domain.repository.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LeaderboardViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val configRepository: AppConfigRepository,
    private val languageStorage: LanguageStorage
) : ViewModel() {

    private val _state = MutableStateFlow(LeaderboardUiState())
    val state = _state.asStateFlow()
    private val _navigation = MutableSharedFlow<LeaderboardNavigationEvent>()
    val navigation = _navigation.asSharedFlow()
    private var syncJob: Job? = null

    init {
        observeLeaderboard()
        observeConfig()
        syncLeaderboard()
    }

    private fun observeLeaderboard() {
        viewModelScope.launch {
            val currentUserId = authRepository.getAuthenticatedUserId() ?: ""
            userRepository.getUsers(100, currentUserId)
                .collect { users ->
                _state.update { it.copy(isLoading = false, leaderboard = users) }
            }
        }
    }

    private fun observeConfig() {
        viewModelScope.launch {
            configRepository.getConfig("leaderboard_info_text")
                .collect { config ->
                    if (config != null) {
                        val language = languageStorage.load()
                        val text = if (language == "TR") config.valueTr else config.valueEn
                        _state.update { it.copy(infoText = text) }
                    }
                }
        }
    }

    private fun syncLeaderboard(isRefreshing: Boolean = false) {
        if (syncJob?.isActive == true) return

        syncJob = viewModelScope.launch {
            _state.update { it.copy(isRefreshing = isRefreshing, error = null) }

            configRepository.syncConfig("leaderboard_info_text")

            val currentUserId = authRepository.getAuthenticatedUserId()
            userRepository.syncUsers(100, currentUserId)
                .onSuccess {
                    _state.update { it.copy(isRefreshing = false) }
                }
                .onFailure { e ->
                    _state.update { 
                        it.copy(isRefreshing = false, error = AppErrorMapper.map(e))
                    }
                }
        }
    }

    fun onAction(action: LeaderboardUiAction) {
        when (action) {
            LeaderboardUiAction.OnBackClicked -> navigateTo(LeaderboardNavigationEvent.Back)
            is LeaderboardUiAction.OnUserClicked -> navigateTo(LeaderboardNavigationEvent.ToUserProfile(action.userId))
            LeaderboardUiAction.OnRefresh -> syncLeaderboard(isRefreshing = true)
            LeaderboardUiAction.OnErrorShown -> _state.update { it.copy(error = null) }
        }
    }

    private fun navigateTo(event: LeaderboardNavigationEvent) {
        viewModelScope.launch { _navigation.emit(event) }
    }
}
