package com.berkekucuk.mmaapp.presentation.screens.event_comments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.berkekucuk.mmaapp.core.app.Route
import com.berkekucuk.mmaapp.core.utils.AppErrorMapper
import com.berkekucuk.mmaapp.domain.enums.BanAction
import com.berkekucuk.mmaapp.domain.enums.BanScope
import com.berkekucuk.mmaapp.domain.enums.CommentsSuccessMessage
import com.berkekucuk.mmaapp.domain.model.AuthState
import com.berkekucuk.mmaapp.domain.repository.AuthRepository
import com.berkekucuk.mmaapp.domain.repository.EventCommentRepository
import com.berkekucuk.mmaapp.domain.repository.UserRepository
import com.berkekucuk.mmaapp.domain.usecase.ObserveBlockedUserIdsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventCommentsViewModel(
    private val commentRepository: EventCommentRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val observeBlockedUserIds: ObserveBlockedUserIdsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<Route.EventComments>()
    private val eventId: String = route.eventId
    private val _state = MutableStateFlow(EventCommentsUiState(eventName = route.eventName))
    val state = _state.asStateFlow()
    private val _navigation = MutableSharedFlow<EventCommentsNavigationEvent>()
    val navigation = _navigation.asSharedFlow()

    init {
        observeModerationRole()
        observeComments()
        refreshComments()
        listenForCommentChanges()
    }

    private fun observeModerationRole() {
        viewModelScope.launch {
            authRepository.authState
                .map { (it as? AuthState.Authenticated)?.role?.canModerate == true }
                .distinctUntilChanged()
                .collect { canModerate ->
                    _state.update { it.copy(canModerate = canModerate) }
                }
        }
    }

    private fun observeComments() {
        viewModelScope.launch {
            combine(
                commentRepository.getComments(eventId),
                authRepository.currentUserId,
                observeBlockedUserIds()
            ) { comments, userId, blockedUserIds ->
                userId to comments.filterNot { it.userId in blockedUserIds }
            }.collect { (userId, comments) ->
                _state.update {
                    it.copy(
                        currentUserId = userId,
                        comments = comments,
                        isLoading = it.isLoading && comments.isEmpty(),
                    )
                }
            }
        }
    }

    private fun refreshComments() {
        viewModelScope.launch {
            commentRepository.refreshComments(eventId)
                .onFailure { e ->
                    val error = if (_state.value.comments.isEmpty()) AppErrorMapper.map(e) else null
                    _state.update { it.copy(error = error) }
                }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun listenForCommentChanges() {
        viewModelScope.launch {
            commentRepository.listenForCommentChanges(eventId)
        }
    }

    fun onAction(action: EventCommentsUiAction) {
        when (action) {
            is EventCommentsUiAction.OnBackClicked -> navigateTo(EventCommentsNavigationEvent.Back)
            is EventCommentsUiAction.OnRetryClicked -> refreshComments()
            is EventCommentsUiAction.OnLoadOlderComments -> loadOlderComments()
            is EventCommentsUiAction.OnCommentInputChanged -> _state.update { it.copy(commentInput = action.text.take(MAX_COMMENT_LENGTH)) }
            is EventCommentsUiAction.OnSendClicked -> sendComment()
            is EventCommentsUiAction.OnUserClicked -> navigateTo(EventCommentsNavigationEvent.ToUserProfile(action.userId))
            is EventCommentsUiAction.OnDeleteClicked -> _state.update { it.copy(pendingDeleteCommentId = action.commentId) }
            is EventCommentsUiAction.OnConfirmDelete -> deleteComment()
            is EventCommentsUiAction.OnDismissDeleteDialog -> _state.update { it.copy(pendingDeleteCommentId = null) }
            is EventCommentsUiAction.OnReportClicked -> _state.update { it.copy(pendingReportCommentId = action.commentId, selectedReportReason = null) }
            is EventCommentsUiAction.OnReportReasonChanged -> _state.update { it.copy(selectedReportReason = action.reason) }
            is EventCommentsUiAction.OnSubmitReport -> reportComment()
            is EventCommentsUiAction.OnDismissReportDialog -> _state.update { it.copy(pendingReportCommentId = null, selectedReportReason = null) }
            is EventCommentsUiAction.OnBanClicked -> _state.update { it.copy(pendingBanUserId = action.userId, pendingBanAction = BanAction.BAN, selectedBanScope = BanScope.THIS_EVENT) }
            is EventCommentsUiAction.OnUnbanClicked -> _state.update { it.copy(pendingBanUserId = action.userId, pendingBanAction = BanAction.UNBAN, selectedBanScope = BanScope.THIS_EVENT) }
            is EventCommentsUiAction.OnBanScopeChanged -> _state.update { it.copy(selectedBanScope = action.scope) }
            is EventCommentsUiAction.OnConfirmBan -> submitBan()
            is EventCommentsUiAction.OnDismissBanDialog -> _state.update { it.copy(pendingBanUserId = null, pendingBanAction = null) }
            is EventCommentsUiAction.OnMessageShown -> _state.update { it.copy(error = null, successMessage = null) }
        }
    }

    private fun loadOlderComments() {
        val currentState = _state.value
        if (currentState.isLoading || currentState.isLoadingOlderComments || !currentState.hasOlderComments) return

        viewModelScope.launch {
            _state.update { it.copy(isLoadingOlderComments = true) }
            commentRepository.loadOlderComments(eventId)
                .onSuccess { hasMore ->
                    _state.update { it.copy(isLoadingOlderComments = false, hasOlderComments = hasMore) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoadingOlderComments = false, error = AppErrorMapper.map(e)) }
                }
        }
    }

    private fun sendComment() {
        val currentState = _state.value
        val content = currentState.commentInput.trim()
        if (content.isEmpty() || currentState.isSending) return

        viewModelScope.launch {
            _state.update { it.copy(isSending = true, error = null) }
            commentRepository.addComment(eventId, content)
                .onSuccess {
                    _state.update {
                        it.copy(
                            commentInput = "",
                            isSending = false,
                            scrollToLatestTrigger = it.scrollToLatestTrigger + 1,
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isSending = false, error = AppErrorMapper.map(e)) }
                }
        }
    }

    private fun deleteComment() {
        val commentId = _state.value.pendingDeleteCommentId ?: return
        _state.update { it.copy(pendingDeleteCommentId = null, error = null) }

        viewModelScope.launch {
            commentRepository.deleteComment(eventId, commentId)
                .onFailure { e ->
                    _state.update { it.copy(error = AppErrorMapper.map(e)) }
                }
        }
    }

    private fun reportComment() {
        val currentState = _state.value
        val commentId = currentState.pendingReportCommentId ?: return
        val reason = currentState.selectedReportReason ?: return

        viewModelScope.launch {
            _state.update { it.copy(isReporting = true, error = null) }
            userRepository.reportComment(commentId, reason.dbValue)
                .onSuccess {
                    _state.update {
                        it.copy(isReporting = false, pendingReportCommentId = null, selectedReportReason = null, successMessage = CommentsSuccessMessage.REPORT_SENT)
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(isReporting = false, pendingReportCommentId = null, selectedReportReason = null, error = AppErrorMapper.map(e))
                    }
                }
        }
    }

    private fun submitBan() {
        val currentState = _state.value
        val userId = currentState.pendingBanUserId ?: return
        val action = currentState.pendingBanAction ?: return
        val banEventId = if (currentState.selectedBanScope == BanScope.THIS_EVENT) eventId else null

        viewModelScope.launch {
            _state.update { it.copy(isSubmittingBan = true, error = null) }
            val result = when (action) {
                BanAction.BAN -> commentRepository.banUser(userId, banEventId)
                BanAction.UNBAN -> commentRepository.unbanUser(userId, banEventId)
            }
            result
                .onSuccess {
                    val message = when (action) {
                        BanAction.BAN -> CommentsSuccessMessage.USER_BANNED
                        BanAction.UNBAN -> CommentsSuccessMessage.USER_UNBANNED
                    }
                    _state.update {
                        it.copy(isSubmittingBan = false, pendingBanUserId = null, pendingBanAction = null, successMessage = message)
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(isSubmittingBan = false, pendingBanUserId = null, pendingBanAction = null, error = AppErrorMapper.map(e))
                    }
                }
        }
    }

    private fun navigateTo(event: EventCommentsNavigationEvent) {
        viewModelScope.launch {
            _navigation.emit(event)
        }
    }

    companion object {
        const val MAX_COMMENT_LENGTH = 500
    }
}
