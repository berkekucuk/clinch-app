package com.berkekucuk.mmaapp.presentation.screens.fighter_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.berkekucuk.mmaapp.core.app.Route
import com.berkekucuk.mmaapp.core.utils.AppErrorMapper
import com.berkekucuk.mmaapp.domain.repository.AuthRepository
import com.berkekucuk.mmaapp.domain.repository.FighterRepository
import com.berkekucuk.mmaapp.domain.repository.InteractionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FighterDetailViewModel(
    private val repository: FighterRepository,
    private val authRepository: AuthRepository,
    private val interactionRepository: InteractionRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<Route.FighterDetail>()
    private val fighterId: String = route.fighterId
    private val _state = MutableStateFlow(FighterDetailUiState())
    val state = _state.asStateFlow()
    private val _navigation = MutableSharedFlow<FighterDetailNavigationEvent>()
    val navigation = _navigation.asSharedFlow()
    private var syncJob: Job? = null

    init {
        observeFighter()
        syncFighter()
        observeFavoriteStatus()
    }

    private fun observeFighter() {
        viewModelScope.launch {
            repository.getFighter(fighterId)
                .collect { fighter ->
                    _state.update {
                        it.copy(
                            fighter = fighter,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun syncFighter(isRefreshing: Boolean = false) {
        if (syncJob?.isActive == true) return

        syncJob = viewModelScope.launch {
            _state.update { it.copy(isRefreshing = isRefreshing, error = null) }
            repository.syncFighter(fighterId)
                .onSuccess {
                    _state.update { it.copy(isRefreshing = false) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, isRefreshing = false, error = AppErrorMapper.map(e)) }
                }
        }
    }

    private fun observeFavoriteStatus() {
        viewModelScope.launch {
            val userId = authRepository.getAuthenticatedUserId() ?: return@launch
            interactionRepository.getInteractions(userId, "favorite")
                .collect { interactions ->
                val match = interactions.find { it.fighterId == fighterId }
                _state.update { it.copy(
                    isFavorite = match != null,
                    favoriteInteractionId = match?.id
                )}}
        }
    }

    private fun toggleFavorite() {
        viewModelScope.launch {
            val userId = authRepository.getAuthenticatedUserId() ?: return@launch
            val currentState = _state.value

            if (currentState.isFavorite && currentState.favoriteInteractionId != null) {
                interactionRepository.removeInteraction(currentState.favoriteInteractionId)
            } else {
                interactionRepository.addInteraction(userId, fighterId, "favorite")
            }
        }
    }

    fun onAction(action: FighterDetailUiAction) {
        when (action) {
            is FighterDetailUiAction.OnFightClicked -> navigateTo(
                FighterDetailNavigationEvent.ToFightDetail(action.fightId, fighterId)
            )
            is FighterDetailUiAction.OnBackClicked -> navigateTo(FighterDetailNavigationEvent.Back)
            is FighterDetailUiAction.OnRefresh -> syncFighter(isRefreshing = true)
            is FighterDetailUiAction.OnToggleFavorite -> toggleFavorite()
        }
    }

    private fun navigateTo(event: FighterDetailNavigationEvent) {
        viewModelScope.launch {
            _navigation.emit(event)
        }
    }
}