package com.berkekucuk.mmaapp.presentation.screens.send_notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkekucuk.mmaapp.core.utils.AppErrorMapper
import com.berkekucuk.mmaapp.core.utils.NotificationDefaults
import com.berkekucuk.mmaapp.data.remote.supabase.AdminNotificationService
import com.berkekucuk.mmaapp.domain.repository.AuthRepository
import com.berkekucuk.mmaapp.domain.repository.EventRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SendNotificationViewModel(
    private val adminNotificationService: AdminNotificationService,
    private val eventRepository: EventRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SendNotificationUiState())
    val state: StateFlow<SendNotificationUiState> = _state.asStateFlow()
    private val _navigation = MutableSharedFlow<SendNotificationNavigationEvent>()
    val navigation = _navigation.asSharedFlow()

    init {
        loadDefaults()
    }

    private fun loadDefaults() {
        viewModelScope.launch {
            val currentUserId = authRepository.getAuthenticatedUserId()
            if (!currentUserId.isNullOrBlank()) {
                _state.update { it.copy(userIds = currentUserId) }
            }
            val event = eventRepository.getNearestActiveEvent() ?: return@launch
            _state.update {
                it.copy(
                    eventId = event.eventId,
                    title = NotificationDefaults.titleFor(event.name),
                )
            }
        }
    }

    fun onAction(action: SendNotificationUiAction) {
        when (action) {
            is SendNotificationUiAction.OnTitleChanged -> _state.update { it.copy(title = action.value) }
            is SendNotificationUiAction.OnBodyChanged -> _state.update { it.copy(body = action.value) }
            is SendNotificationUiAction.OnEventIdChanged -> _state.update { it.copy(eventId = action.value) }
            is SendNotificationUiAction.OnTargetChanged -> _state.update { it.copy(target = action.value) }
            is SendNotificationUiAction.OnUserIdsChanged -> _state.update { it.copy(userIds = action.value) }
            SendNotificationUiAction.OnSendClicked -> _state.update { it.copy(showConfirmDialog = true) }
            SendNotificationUiAction.OnDismissDialog -> _state.update { it.copy(showConfirmDialog = false) }
            SendNotificationUiAction.OnConfirmSend -> sendNotification()
            SendNotificationUiAction.OnSnackbarDismissed -> _state.update { it.copy(error = null) }
            SendNotificationUiAction.OnBackClicked -> navigateTo(SendNotificationNavigationEvent.Back)
        }
    }

    private fun sendNotification() {
        val current = _state.value
        if (!current.canSend) return

        _state.update { it.copy(showConfirmDialog = false, isSending = true) }
        viewModelScope.launch {
            adminNotificationService.sendManualNotification(
                title = current.title.trim(),
                body = current.body.trim(),
                eventId = current.eventId.trim().ifEmpty { null },
                target = current.target,
                userIds = current.parsedUserIds,
            )
                .onSuccess {
                    _state.update { it.copy(isSending = false) }
                }
                .onFailure { e -> _state.update { it.copy(isSending = false, error = AppErrorMapper.map(e)) } }
        }
    }

    private fun navigateTo(event: SendNotificationNavigationEvent) {
        viewModelScope.launch {
            _navigation.emit(event)
        }
    }
}
