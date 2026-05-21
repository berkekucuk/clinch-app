package com.berkekucuk.mmaapp.presentation.screens.fighter_search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.berkekucuk.mmaapp.core.app.Route
import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.domain.model.Fighter
import com.berkekucuk.mmaapp.domain.repository.AuthRepository
import com.berkekucuk.mmaapp.domain.repository.FighterRepository
import com.berkekucuk.mmaapp.domain.repository.InteractionRepository
import com.berkekucuk.mmaapp.domain.repository.WeightClassRepository
import com.berkekucuk.mmaapp.core.utils.AppErrorMapper
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class FighterSearchViewModel(
    private val fighterRepository: FighterRepository,
    private val weightClassRepository: WeightClassRepository,
    private val interactionRepository: InteractionRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val MENS_P4P_ID = "mens_p4p"
    }

    private val route = savedStateHandle.toRoute<Route.FighterSearch>()
    private val interactionType: String? = route.interactionType

    private val _state = MutableStateFlow(FighterSearchUiState())
    val state = _state.asStateFlow()

    private val _navigation = MutableSharedFlow<FighterSearchNavigationEvent>()
    val navigation = _navigation.asSharedFlow()

    private var p4pFighters: List<Fighter> = emptyList()

    init {
        observeP4PFighters()
        observeSearchQuery()
    }

    private fun observeP4PFighters() {
        viewModelScope.launch {
            weightClassRepository.getWeightClassById(MENS_P4P_ID)
                .collect { weightClass ->
                p4pFighters = weightClass?.rankings?.mapNotNull { it.fighter } ?: emptyList()
                if (_state.value.query.length < 2) {
                    _state.update { it.copy(results = p4pFighters) }
                }
            }
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _state
                .map { it.query }
                .distinctUntilChanged()
                .debounce(500)
                .collectLatest { query ->
                    if (query.length >= 2) {
                        search(query)
                    } else {
                        _state.update { it.copy(results = p4pFighters, error = null) }
                    }
                }
        }
    }

    private suspend fun search(query: String) {
        _state.update { it.copy(error = null) }
        fighterRepository.searchFighters(query)
            .onSuccess { results ->
                _state.update { it.copy(results = results) }
            }
            .onFailure { e ->
                _state.update { it.copy(error = AppErrorMapper.map(e)) }
            }
    }

    fun onAction(action: FighterSearchUiAction) {
        when (action) {
            is FighterSearchUiAction.OnQueryChanged -> _state.update {
                it.copy(query = action.query, error = null)
            }
            is FighterSearchUiAction.OnClearQuery -> _state.update {
                it.copy(query = "", results = p4pFighters, error = null)
            }
            is FighterSearchUiAction.OnFighterClicked -> {
                if (interactionType != null) {
                    val fighter = _state.value.results.find { it.fighterId == action.fighterId }
                    if (fighter != null) {
                        addInteraction(fighter)
                    }
                } else {
                    navigateTo(FighterSearchNavigationEvent.ToFighterDetail(action.fighterId))
                }
            }
            is FighterSearchUiAction.OnBackClicked -> navigateTo(FighterSearchNavigationEvent.Back)
        }
    }

    private fun addInteraction(fighter: Fighter) {
        val type = interactionType ?: return
        viewModelScope.launch {
            _state.update { it.copy(error = null) }
            
            val userId = authRepository.getAuthenticatedUserId()
            if (userId == null) {
                _state.update { it.copy(error = AppError.UNAUTHENTICATED) }
                return@launch
            }

            interactionRepository.addInteraction(
                userId = userId,
                interactionType = type,
                fighter = fighter,
            ).onSuccess {
                navigateTo(FighterSearchNavigationEvent.Back)
            }.onFailure { e ->
                _state.update { it.copy(error = AppErrorMapper.map(e)) }
            }
        }
    }

    private fun navigateTo(event: FighterSearchNavigationEvent) {
        viewModelScope.launch { _navigation.emit(event) }
    }
}
