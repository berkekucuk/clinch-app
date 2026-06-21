package com.berkekucuk.mmaapp.presentation.screens.user_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkekucuk.mmaapp.core.utils.AppErrorMapper
import com.berkekucuk.mmaapp.domain.repository.UserRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class UserSearchViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UserSearchUiState())
    val state: StateFlow<UserSearchUiState> = _state.asStateFlow()

    private val _navigation = MutableSharedFlow<UserSearchNavigationEvent>()
    val navigation: SharedFlow<UserSearchNavigationEvent> = _navigation.asSharedFlow()

    init {
        observeSearchQuery()
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _state
                .map { it.query }
                .distinctUntilChanged()
                .debounce(500.milliseconds)
                .collectLatest { query ->
                    if (query.length >= 2) {
                        searchUsers(query)
                    } else {
                        _state.update { it.copy(results = emptyList(), error = null) }
                    }
                }
        }
    }

    private suspend fun searchUsers(query: String) {
        _state.update { it.copy(error = null) }

        userRepository.searchUsers(query)
            .onSuccess { users ->
                _state.update {
                    it.copy(results = users)
                }
            }
            .onFailure { e ->
                _state.update {
                    it.copy(error = AppErrorMapper.map(e))
                }
            }
    }

    fun onAction(action: UserSearchUiAction) {
        when (action) {
            is UserSearchUiAction.OnQueryChanged -> {
                _state.update { it.copy(query = action.query, error = null) }
            }
            UserSearchUiAction.OnClearQuery -> {
                _state.update { it.copy(query = "", results = emptyList(), error = null) }
            }
            is UserSearchUiAction.OnUserClicked -> {
                viewModelScope.launch {
                    _navigation.emit(UserSearchNavigationEvent.ToUserProfile(action.userId))
                }
            }
            UserSearchUiAction.OnBackClicked -> {
                viewModelScope.launch {
                    _navigation.emit(UserSearchNavigationEvent.Back)
                }
            }
        }
    }
}