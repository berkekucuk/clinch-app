package com.berkekucuk.mmaapp.presentation.screens.event_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.berkekucuk.mmaapp.core.app.Route
import com.berkekucuk.mmaapp.core.utils.AppErrorMapper
import com.berkekucuk.mmaapp.domain.repository.EventRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val eventRepository: EventRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<Route.EventDetail>()
    private val eventId: String = route.eventId
    private val _state = MutableStateFlow(EventDetailUiState(isLoading = route.fromFightDetail))
    val state = _state.asStateFlow()
    private val _navigation = MutableSharedFlow<EventDetailNavigationEvent>()
    val navigation = _navigation.asSharedFlow()

    init {
        observeEvent()
        if (route.fromFightDetail) {
            syncEvent()
        }
    }

    private fun observeEvent() {
        viewModelScope.launch {
            eventRepository.getEventById(eventId)
                .collect { event ->
                    _state.update {
                        it.copy(
                            event = event,
                            isLoading = false,
                        )
                    }
                }
        }
    }

    fun onAction(action: EventDetailUiAction){
        when(action){
            is EventDetailUiAction.OnFightClicked -> navigateTo(EventDetailNavigationEvent.ToFightDetail(action.fightId))
            is EventDetailUiAction.OnBackClicked -> navigateTo(EventDetailNavigationEvent.Back)
            is EventDetailUiAction.OnRefresh -> syncEvent(isRefreshing = true)
        }
    }

    private fun syncEvent(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = isRefreshing, error = null) }
            eventRepository.syncEventById(eventId)
                .onSuccess {
                    _state.update { it.copy(isRefreshing = false) }
                }
                .onFailure { e ->
                    val error = if (!eventRepository.hasEventById(eventId)) {
                        AppErrorMapper.map(e)
                    } else null
                    _state.update { it.copy(isRefreshing = false, isLoading = false, error = error) }
                }
        }
    }

    private fun navigateTo(event: EventDetailNavigationEvent) {
        viewModelScope.launch {
            _navigation.emit(event)
        }
    }
}
