package com.berkekucuk.mmaapp.presentation.screens.news_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.berkekucuk.mmaapp.core.app.Route
import com.berkekucuk.mmaapp.core.utils.AppErrorMapper
import com.berkekucuk.mmaapp.domain.model.AuthState
import com.berkekucuk.mmaapp.domain.model.NewsComment
import com.berkekucuk.mmaapp.domain.repository.AuthRepository
import com.berkekucuk.mmaapp.domain.repository.NewsCommentRepository
import com.berkekucuk.mmaapp.domain.repository.NewsRepository
import com.berkekucuk.mmaapp.domain.repository.UserRepository
import com.berkekucuk.mmaapp.domain.usecase.ObserveBlockedUserIdsUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewsDetailViewModel(
    private val repository: NewsRepository,
    private val commentRepository: NewsCommentRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val observeBlockedUserIds: ObserveBlockedUserIdsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val articleId = savedStateHandle.toRoute<Route.NewsDetail>().articleId

    private val _state = MutableStateFlow(NewsDetailUiState())
    val state: StateFlow<NewsDetailUiState> = _state.asStateFlow()
    private val _navigation = MutableSharedFlow<NewsDetailNavigationEvent>()
    val navigation = _navigation.asSharedFlow()

    init {
        observeArticle()
        observeModerationRole()
        observeComments()
        refreshComments()
    }

    private fun observeArticle() {
        viewModelScope.launch {
            repository.getArticle(articleId)
                .collect { article ->
                    _state.update { it.copy(article = article) }
                }
        }
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
                commentRepository.getComments(articleId),
                authRepository.currentUserId,
                observeBlockedUserIds()
            ) { allComments, userId, blockedUserIds ->
                val visible = allComments.filterNot { it.userId in blockedUserIds }
                val replies = visible
                    .filter { it.parentId != null }
                    .groupBy { it.parentId!! }
                val comments = visible
                    .filter { it.parentId == null }
                    .reversed()
                CommentsSnapshot(userId, comments, replies)
            }.collect { snapshot ->
                _state.update {
                    it.copy(
                        currentUserId = snapshot.userId,
                        comments = snapshot.comments,
                        replies = snapshot.replies,
                    )
                }
            }
        }
    }

    private fun refreshComments(force: Boolean = false) {
        viewModelScope.launch {
            commentRepository.refreshComments(articleId, force)
                .onSuccess { hasMore ->
                    _state.update { it.copy(hasOlderComments = hasMore, commentsLoadFailed = false) }
                }
                .onFailure { e ->
                    val isEmpty = _state.value.comments.isEmpty()
                    val error = if (isEmpty) AppErrorMapper.map(e) else null
                    _state.update { it.copy(error = error, commentsLoadFailed = isEmpty) }
                }
            _state.update { it.copy(isLoadingComments = false) }
        }
    }

    fun onAction(action: NewsDetailUiAction) {
        when (action) {
            is NewsDetailUiAction.OnBackClicked -> navigateTo(NewsDetailNavigationEvent.Back)
            is NewsDetailUiAction.OnRefresh -> refresh()
            is NewsDetailUiAction.OnLoadOlderComments -> loadOlderComments()
            is NewsDetailUiAction.OnCommentInputChanged -> _state.update { it.copy(commentInput = action.text.take(MAX_COMMENT_LENGTH)) }
            is NewsDetailUiAction.OnSendClicked -> sendComment()
            is NewsDetailUiAction.OnUserClicked -> navigateTo(NewsDetailNavigationEvent.ToUserProfile(action.userId))
            is NewsDetailUiAction.OnDeleteClicked -> _state.update { it.copy(pendingDeleteComment = action.comment) }
            is NewsDetailUiAction.OnReplyClicked -> _state.update {
                it.copy(replyingTo = action.comment, focusInputTrigger = it.focusInputTrigger + 1)
            }
            is NewsDetailUiAction.OnCancelReply -> _state.update { it.copy(replyingTo = null) }
            is NewsDetailUiAction.OnToggleReplies -> toggleReplies(action.parentId)
            is NewsDetailUiAction.OnConfirmDelete -> deleteComment()
            is NewsDetailUiAction.OnDismissDeleteDialog -> _state.update { it.copy(pendingDeleteComment = null) }
            is NewsDetailUiAction.OnReportClicked -> _state.update { it.copy(pendingReportCommentId = action.commentId, selectedReportReason = null) }
            is NewsDetailUiAction.OnReportReasonChanged -> _state.update { it.copy(selectedReportReason = action.reason) }
            is NewsDetailUiAction.OnSubmitReport -> reportComment()
            is NewsDetailUiAction.OnDismissReportDialog -> _state.update { it.copy(pendingReportCommentId = null, selectedReportReason = null) }
            is NewsDetailUiAction.OnMessageShown -> _state.update { it.copy(error = null, isReportSent = false) }
        }
    }

    private fun refresh() {
        if (_state.value.isRefreshing) return

        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }

            val articleSync = async { repository.syncArticle(articleId) }
            val commentsSync = async { commentRepository.refreshComments(articleId) }

            articleSync.await()
            commentsSync.await()
                .onSuccess { hasMore ->
                    _state.update { it.copy(hasOlderComments = hasMore, commentsLoadFailed = false) }
                }
                .onFailure {
                    _state.update { it.copy(commentsLoadFailed = it.comments.isEmpty()) }
                }

            _state.update { it.copy(isRefreshing = false) }
        }
    }

    private fun loadOlderComments() {
        val currentState = _state.value
        if (currentState.isLoadingComments || currentState.isLoadingOlderComments || !currentState.hasOlderComments) return

        viewModelScope.launch {
            _state.update { it.copy(isLoadingOlderComments = true) }
            commentRepository.loadOlderComments(articleId)
                .onSuccess { hasMore ->
                    _state.update { it.copy(isLoadingOlderComments = false, hasOlderComments = hasMore) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoadingOlderComments = false, error = AppErrorMapper.map(e)) }
                }
        }
    }

    private fun toggleReplies(parentId: String) {
        _state.update {
            val expanded = it.expandedReplyParentIds
            it.copy(expandedReplyParentIds = if (parentId in expanded) expanded - parentId else expanded + parentId)
        }
    }

    private fun sendComment() {
        val currentState = _state.value
        val content = currentState.commentInput.trim()
        if (content.isEmpty() || currentState.isSending) return
        val replyingTo = currentState.replyingTo

        viewModelScope.launch {
            _state.update { it.copy(isSending = true, error = null) }
            commentRepository.addComment(articleId, content, replyToCommentId = replyingTo?.id)
                .onSuccess {
                    repository.adjustCommentCount(articleId, delta = 1)
                    val rootId = replyingTo?.let { it.parentId ?: it.id }
                    _state.update {
                        it.copy(
                            commentInput = "",
                            isSending = false,
                            replyingTo = null,
                            expandedReplyParentIds = if (rootId != null) it.expandedReplyParentIds + rootId else it.expandedReplyParentIds,
                            scrollToCommentsTrigger = if (rootId == null) it.scrollToCommentsTrigger + 1 else it.scrollToCommentsTrigger,
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isSending = false, error = AppErrorMapper.map(e)) }
                }
        }
    }

    private fun deleteComment() {
        val comment = _state.value.pendingDeleteComment ?: return
        _state.update { it.copy(pendingDeleteComment = null, error = null) }

        viewModelScope.launch {
            commentRepository.deleteComment(articleId, comment)
                .onSuccess { removedCount -> repository.adjustCommentCount(articleId, delta = -removedCount) }
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
            userRepository.reportNewsComment(commentId, reason.dbValue)
                .onSuccess {
                    _state.update {
                        it.copy(isReporting = false, pendingReportCommentId = null, selectedReportReason = null, isReportSent = true)
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(isReporting = false, pendingReportCommentId = null, selectedReportReason = null, error = AppErrorMapper.map(e))
                    }
                }
        }
    }

    private fun navigateTo(event: NewsDetailNavigationEvent) {
        viewModelScope.launch {
            _navigation.emit(event)
        }
    }

    private data class CommentsSnapshot(
        val userId: String?,
        val comments: List<NewsComment>,
        val replies: Map<String, List<NewsComment>>,
    )

    companion object {
        const val MAX_COMMENT_LENGTH = 500
    }
}
