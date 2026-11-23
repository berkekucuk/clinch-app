package com.berkekucuk.mmaapp.presentation.screens.event_comments

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.berkekucuk.mmaapp.core.utils.rememberLocalizedDateStrings
import com.berkekucuk.mmaapp.core.utils.toRelativeTime
import com.berkekucuk.mmaapp.presentation.components.AppSnackbar
import com.berkekucuk.mmaapp.presentation.components.comment.CommentDeleteDialog
import com.berkekucuk.mmaapp.presentation.components.comment.CommentInputBar
import com.berkekucuk.mmaapp.presentation.components.comment.CommentItem
import com.berkekucuk.mmaapp.presentation.components.comment.CommentReportDialog
import com.berkekucuk.mmaapp.presentation.components.AppSnackbarEffect
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun EventCommentsScreenRoot(
    viewModel: EventCommentsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (userId: String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            when (event) {
                is EventCommentsNavigationEvent.Back -> onNavigateBack()
                is EventCommentsNavigationEvent.ToUserProfile -> onNavigateToProfile(event.userId)
            }
        }
    }

    EventCommentsScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCommentsScreen(
    state: EventCommentsUiState,
    onAction: (EventCommentsUiAction) -> Unit,
) {
    // Theme & Resources
    val dateStrings = rememberLocalizedDateStrings()

    // Compose Core States
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    var now by remember { mutableStateOf(Clock.System.now()) }
    val inputFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // UI Data & Definitions
    val message = AppTheme.strings.mapError(state.error) ?: AppTheme.strings.commentReported.takeIf { state.isReportSent }
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false
            layoutInfo.totalItemsCount > LOAD_MORE_THRESHOLD && lastVisibleIndex >= layoutInfo.totalItemsCount - LOAD_MORE_THRESHOLD
        }
    }

    AppSnackbarEffect(
        message = message,
        snackbarHostState = snackbarHostState,
        duration = SnackbarDuration.Short,
        onDismiss = { onAction(EventCommentsUiAction.OnMessageShown) },
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000.milliseconds)
            now = Clock.System.now()
        }
    }

    LaunchedEffect(shouldLoadMore, state.comments.size) {
        if (shouldLoadMore && state.comments.isNotEmpty()) onAction(EventCommentsUiAction.OnLoadOlderComments)
    }

    LaunchedEffect(state.focusInputTrigger) {
        if (state.focusInputTrigger > 0) {
            runCatching { inputFocusRequester.requestFocus() }
            keyboardController?.show()
        }
    }

    LaunchedEffect(state.scrollToLatestTrigger) {
        if (state.scrollToLatestTrigger > 0) listState.animateScrollToItem(0)
    }

    LaunchedEffect(state.comments.firstOrNull()?.id) {
        if (listState.firstVisibleItemIndex <= 1) listState.animateScrollToItem(0)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppTheme.colors.pagerBackground,
        contentWindowInsets = WindowInsets.statusBars,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    AppSnackbar(
                        snackbarData = snackbarData,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            )
        },
        topBar = {
            Column(
                modifier = Modifier.background(AppTheme.colors.eventDetailTopBar)
            ) {
                TopAppBar(
                    title = {
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = AppTheme.strings.commentsTitle,
                                maxLines = 1,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            if (!state.eventName.isNullOrBlank()) {
                                Text(
                                    text = state.eventName,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 14.sp,
                                    color = AppTheme.colors.textSecondary,
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { onAction(EventCommentsUiAction.OnBackClicked) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = AppTheme.strings.contentDescriptionBack,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        navigationIconContentColor = AppTheme.colors.textPrimary,
                        titleContentColor = AppTheme.colors.textPrimary,
                    ),
                )
            }
        },
        bottomBar = {
            CommentInputBar(
                isAuthenticated = state.currentUserId != null,
                input = state.commentInput,
                isSending = state.isSending,
                maxLength = EventCommentsViewModel.MAX_COMMENT_LENGTH,
                onInputChanged = { onAction(EventCommentsUiAction.OnCommentInputChanged(it)) },
                onSendClick = { onAction(EventCommentsUiAction.OnSendClicked) },
                replyingToUsername = state.replyingTo?.username,
                onCancelReply = { onAction(EventCommentsUiAction.OnCancelReply) },
                focusRequester = inputFocusRequester,
            )
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            reverseLayout = true,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom),
        ) {
            if (state.comments.isEmpty() && state.isLoading) {
                item(contentType = "Loading") {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppTheme.colors.ufcRed)
                    }
                }
            } else if (state.comments.isEmpty() && !state.commentsLoadFailed) {
                item(contentType = "EmptyState") {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = AppTheme.strings.commentsEmpty,
                            color = AppTheme.colors.textSecondary,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                items(
                    items = state.comments,
                    key = { it.id },
                    contentType = { "Comment" }
                ) { comment ->
                    CommentItem(
                        username = comment.username,
                        avatarUrl = comment.avatarUrl,
                        content = comment.content,
                        replyToUsername = comment.replyToUsername,
                        onReplyToUserClick = comment.replyToUserId?.let { userId ->
                            { onAction(EventCommentsUiAction.OnUserClicked(userId)) }
                        },
                        onReplyClick = if (state.currentUserId != null && comment.userId != state.currentUserId) {
                            { onAction(EventCommentsUiAction.OnReplyClicked(comment)) }
                        } else null,
                        timeText = comment.createdAt.toRelativeTime(
                            now = now,
                            justNow = AppTheme.strings.commentJustNow,
                            minutesAgo = AppTheme.strings.commentMinutesAgo,
                            hoursAgo = AppTheme.strings.commentHoursAgo,
                            daysAgo = AppTheme.strings.commentDaysAgo,
                            months = dateStrings.months,
                        ),
                        canReport = state.currentUserId != null && comment.userId != state.currentUserId,
                        canDelete = state.currentUserId != null && (comment.userId == state.currentUserId || state.canModerate),
                        onUserClick = { onAction(EventCommentsUiAction.OnUserClicked(comment.userId)) },
                        onDeleteClick = { onAction(EventCommentsUiAction.OnDeleteClicked(comment.id)) },
                        onReportClick = { onAction(EventCommentsUiAction.OnReportClicked(comment.id)) },
                        modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null),
                    )
                }

                if (state.isLoadingOlderComments) {
                    item(contentType = "LoadingMore") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = AppTheme.colors.ufcRed,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (state.pendingDeleteCommentId != null) {
        CommentDeleteDialog(
            onDismiss = { onAction(EventCommentsUiAction.OnDismissDeleteDialog) },
            onConfirm = { onAction(EventCommentsUiAction.OnConfirmDelete) },
        )
    }

    if (state.pendingReportCommentId != null) {
        CommentReportDialog(
            selectedReason = state.selectedReportReason,
            isReporting = state.isReporting,
            onReasonSelected = { onAction(EventCommentsUiAction.OnReportReasonChanged(it)) },
            onDismiss = { onAction(EventCommentsUiAction.OnDismissReportDialog) },
            onSubmit = { onAction(EventCommentsUiAction.OnSubmitReport) },
        )
    }
}

private const val LOAD_MORE_THRESHOLD = 5
