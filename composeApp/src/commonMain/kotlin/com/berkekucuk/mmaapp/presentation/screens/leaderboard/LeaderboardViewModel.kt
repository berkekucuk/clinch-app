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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi

class LeaderboardViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val configRepository: AppConfigRepository,
    private val languageStorage: LanguageStorage
) : ViewModel() {

    companion object {
        const val PAGE_SIZE = 100
    }

    private val _state = MutableStateFlow(LeaderboardUiState())
    val state = _state.asStateFlow()
    private val _navigation = MutableSharedFlow<LeaderboardNavigationEvent>()
    val navigation = _navigation.asSharedFlow()

    private val currentPageFlow = MutableStateFlow(0)
    
    private var syncJob: Job? = null

    init {
        observeLeaderboard()
        observeConfig()
        syncLeaderboard()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeLeaderboard() {
        viewModelScope.launch {
            val currentUserId = authRepository.getAuthenticatedUserId() ?: ""
            currentPageFlow
                .flatMapLatest { page ->
                    userRepository.getUsers(PAGE_SIZE, page * PAGE_SIZE, currentUserId)
                }
                .collect { users ->
                    val isMaxPage = currentPageFlow.value >= 9
                    val limitedUsers = if (isMaxPage) users.take(99) else users
                    
                    _state.update {
                        it.copy(
                            isLoading = false,
                            leaderboard = limitedUsers,
                            canGoNext = users.size == PAGE_SIZE && !isMaxPage
                        )
                    }
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

            val currentUserId = authRepository.getAuthenticatedUserId()
            val page = currentPageFlow.value
            userRepository.syncUsers(PAGE_SIZE, page * PAGE_SIZE, currentUserId)
                .onSuccess {
                    _state.update { it.copy(isRefreshing = false) }
                }
                .onFailure { e ->
                    _state.update { 
                        it.copy(isRefreshing = false, error = AppErrorMapper.map(e))
                    }
                }
                
            configRepository.syncConfig("leaderboard_info_text")
        }
    }

    fun onAction(action: LeaderboardUiAction) {
        when (action) {
            LeaderboardUiAction.OnBackClicked -> navigateTo(LeaderboardNavigationEvent.Back)
            is LeaderboardUiAction.OnUserClicked -> navigateTo(LeaderboardNavigationEvent.ToUserProfile(action.userId))
            LeaderboardUiAction.OnRefresh -> syncLeaderboard(isRefreshing = true)
            LeaderboardUiAction.OnErrorShown -> _state.update { it.copy(error = null) }
            LeaderboardUiAction.OnNextPage -> nextPage()
            LeaderboardUiAction.OnPreviousPage -> previousPage()
        }
    }

    private fun navigateTo(event: LeaderboardNavigationEvent) {
        viewModelScope.launch { _navigation.emit(event) }
    }

    private fun nextPage() {
        val currentState = _state.value
        if (!currentState.canGoNext || currentState.isRefreshing || syncJob?.isActive == true) return

        val nextPage = currentPageFlow.value + 1
        if (nextPage > 9) return

        _state.update {
            it.copy(
                currentPage = nextPage,
                canGoNext = false
            )
        }

        currentPageFlow.value = nextPage
        syncLeaderboard(isRefreshing = true)
    }

    private fun previousPage() {
        val currentState = _state.value
        if (currentPageFlow.value <= 0 || currentState.isRefreshing || syncJob?.isActive == true) return

        val prevPage = currentPageFlow.value - 1

        _state.update {
            it.copy(
                currentPage = prevPage,
                canGoNext = false
            )
        }

        currentPageFlow.value = prevPage
    }
}
