package com.berkekucuk.mmaapp.presentation.screens.news_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.berkekucuk.mmaapp.core.presentation.AppLanguage
import com.berkekucuk.mmaapp.core.presentation.AppTheme
import com.berkekucuk.mmaapp.core.utils.rememberLocalizedDateStrings
import com.berkekucuk.mmaapp.core.utils.toRelativeTime
import com.berkekucuk.mmaapp.core.utils.toUserFriendlyDate
import com.berkekucuk.mmaapp.domain.model.NewsArticle
import com.berkekucuk.mmaapp.domain.model.NewsComment
import com.berkekucuk.mmaapp.presentation.components.AppSnackbar
import com.berkekucuk.mmaapp.presentation.components.AppSnackbarEffect
import com.berkekucuk.mmaapp.presentation.components.appClickable
import com.berkekucuk.mmaapp.presentation.components.comment.CommentDeleteDialog
import com.berkekucuk.mmaapp.presentation.components.comment.CommentInputBar
import com.berkekucuk.mmaapp.presentation.components.comment.CommentItem
import com.berkekucuk.mmaapp.presentation.components.comment.CommentItemStyle
import com.berkekucuk.mmaapp.presentation.components.comment.CommentReportDialog
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun NewsDetailScreenRoot(
    viewModel: NewsDetailViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (userId: String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            when (event) {
                is NewsDetailNavigationEvent.Back -> onNavigateBack()
                is NewsDetailNavigationEvent.ToUserProfile -> onNavigateToProfile(event.userId)
            }
        }
    }

    NewsDetailScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    state: NewsDetailUiState,
    onAction: (NewsDetailUiAction) -> Unit
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
        onDismiss = { onAction(NewsDetailUiAction.OnMessageShown) },
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000.milliseconds)
            now = Clock.System.now()
        }
    }

    LaunchedEffect(shouldLoadMore, state.comments.size) {
        if (shouldLoadMore && state.comments.isNotEmpty()) onAction(NewsDetailUiAction.OnLoadOlderComments)
    }

    LaunchedEffect(state.focusInputTrigger) {
        if (state.focusInputTrigger > 0) {
            runCatching { inputFocusRequester.requestFocus() }
            keyboardController?.show()
        }
    }

    LaunchedEffect(state.scrollToCommentsTrigger) {
        if (state.scrollToCommentsTrigger > 0) listState.animateScrollToItem(COMMENTS_HEADER_INDEX)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppTheme.colors.pagerBackground,
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
            TopAppBar(
                title = {
                    Text(
                        text = AppTheme.strings.newsDetailTitle,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(NewsDetailUiAction.OnBackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = AppTheme.strings.contentDescriptionBack
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.topBarBackground,
                    navigationIconContentColor = AppTheme.colors.textPrimary,
                    titleContentColor = AppTheme.colors.textPrimary,
                )
            )
        },
        bottomBar = {
            CommentInputBar(
                isAuthenticated = state.currentUserId != null,
                input = state.commentInput,
                isSending = state.isSending,
                maxLength = NewsDetailViewModel.MAX_COMMENT_LENGTH,
                onInputChanged = { onAction(NewsDetailUiAction.OnCommentInputChanged(it)) },
                onSendClick = { onAction(NewsDetailUiAction.OnSendClicked) },
                replyingToUsername = state.replyingTo?.username,
                onCancelReply = { onAction(NewsDetailUiAction.OnCancelReply) },
                focusRequester = inputFocusRequester,
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onAction(NewsDetailUiAction.OnRefresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val article = state.article ?: return@PullToRefreshBox

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
            ) {
                item(key = "article", contentType = "Article") {
                    NewsArticleContent(article = article)
                }

                item(key = "comments_header", contentType = "CommentsHeader") {
                    CommentsHeader()
                }

                if (state.comments.isEmpty() && state.isLoadingComments) {
                    item(contentType = "CommentsLoading") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = AppTheme.colors.ufcRed,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                } else if (state.comments.isEmpty() && !state.commentsLoadFailed) {
                    item(contentType = "CommentsEmpty") {
                        Text(
                            text = AppTheme.strings.commentsEmpty,
                            color = AppTheme.colors.textSecondary,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 24.dp)
                        )
                    }
                } else {
                    items(
                        items = state.comments,
                        key = { it.id },
                        contentType = { "CommentThread" }
                    ) { comment ->
                        NewsCommentThread(
                            comment = comment,
                            replies = state.replies[comment.id].orEmpty(),
                            isExpanded = comment.id in state.expandedReplyParentIds,
                            state = state,
                            now = now,
                            months = dateStrings.months,
                            onAction = onAction,
                            modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null),
                        )
                    }

                    if (state.isLoadingOlderComments) {
                        item(contentType = "CommentsLoadingMore") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
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
    }

    if (state.pendingDeleteComment != null) {
        CommentDeleteDialog(
            onDismiss = { onAction(NewsDetailUiAction.OnDismissDeleteDialog) },
            onConfirm = { onAction(NewsDetailUiAction.OnConfirmDelete) },
        )
    }

    if (state.pendingReportCommentId != null) {
        CommentReportDialog(
            selectedReason = state.selectedReportReason,
            isReporting = state.isReporting,
            onReasonSelected = { onAction(NewsDetailUiAction.OnReportReasonChanged(it)) },
            onDismiss = { onAction(NewsDetailUiAction.OnDismissReportDialog) },
            onSubmit = { onAction(NewsDetailUiAction.OnSubmitReport) },
        )
    }
}

@Composable
private fun NewsCommentThread(
    comment: NewsComment,
    replies: List<NewsComment>,
    isExpanded: Boolean,
    state: NewsDetailUiState,
    now: Instant,
    months: List<String>,
    onAction: (NewsDetailUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        NewsCommentItem(
            comment = comment,
            state = state,
            now = now,
            months = months,
            onAction = onAction,
            avatarSize = 44.dp,
        )

        AnimatedVisibility(
            visible = isExpanded && replies.isNotEmpty(),
            enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut(),
        ) {
            Column {
                replies.forEach { reply ->
                    key(reply.id) {
                        NewsCommentItem(
                            comment = reply,
                            state = state,
                            now = now,
                            months = months,
                            onAction = onAction,
                            avatarSize = 32.dp,
                            modifier = Modifier.padding(start = REPLY_INDENT),
                        )
                    }
                }
            }
        }

        if (replies.isNotEmpty()) {
            RepliesToggle(
                replyCount = replies.size,
                isExpanded = isExpanded,
                onToggle = { onAction(NewsDetailUiAction.OnToggleReplies(comment.id)) },
            )
        }

        HorizontalDivider(
            color = AppTheme.colors.dividerColor,
            thickness = 0.8.dp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun NewsCommentItem(
    comment: NewsComment,
    state: NewsDetailUiState,
    now: Instant,
    months: List<String>,
    onAction: (NewsDetailUiAction) -> Unit,
    modifier: Modifier = Modifier,
    avatarSize: Dp = 40.dp,
) {
    CommentItem(
        username = comment.username,
        avatarUrl = comment.avatarUrl,
        content = comment.content,
        replyToUsername = comment.replyToUsername,
        onReplyToUserClick = comment.replyToUserId?.let { userId ->
            { onAction(NewsDetailUiAction.OnUserClicked(userId)) }
        },
        timeText = comment.createdAt.toRelativeTime(
            now = now,
            justNow = AppTheme.strings.commentJustNow,
            minutesAgo = AppTheme.strings.commentMinutesAgo,
            hoursAgo = AppTheme.strings.commentHoursAgo,
            daysAgo = AppTheme.strings.commentDaysAgo,
            months = months,
        ),
        canReport = state.currentUserId != null && comment.userId != state.currentUserId,
        canDelete = state.currentUserId != null && (comment.userId == state.currentUserId || state.canModerate),
        onUserClick = { onAction(NewsDetailUiAction.OnUserClicked(comment.userId)) },
        onDeleteClick = { onAction(NewsDetailUiAction.OnDeleteClicked(comment)) },
        onReportClick = { onAction(NewsDetailUiAction.OnReportClicked(comment.id)) },
        onReplyClick = if (state.currentUserId != null) {
            { onAction(NewsDetailUiAction.OnReplyClicked(comment)) }
        } else null,
        avatarSize = avatarSize,
        style = CommentItemStyle.Flat,
        modifier = modifier,
    )
}

@Composable
private fun RepliesToggle(
    replyCount: Int,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = THREAD_TEXT_START, top = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            color = AppTheme.colors.textSecondary.copy(alpha = 0.5f),
            thickness = 1.dp,
            modifier = Modifier.width(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isExpanded) AppTheme.strings.commentHideReplies else AppTheme.strings.commentViewReplies(replyCount),
            color = AppTheme.colors.textSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .appClickable(onClick = onToggle)
                .padding(vertical = 6.dp)
        )
    }
}

@Composable
private fun CommentsHeader() {
    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(color = AppTheme.colors.dividerColor, thickness = 0.8.dp)
        Text(
            text = AppTheme.strings.commentsTitle,
            color = AppTheme.colors.textPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp)
        )
        HorizontalDivider(color = AppTheme.colors.dividerColor, thickness = 0.8.dp)
    }
}

@Composable
private fun NewsArticleContent(
    article: NewsArticle,
) {
    val uriHandler = LocalUriHandler.current
    val dateStrings = rememberLocalizedDateStrings()
    val isTurkish = AppTheme.strings.language == AppLanguage.TR
    val title = if (isTurkish) article.titleTr else article.titleEn
    val summary = if (isTurkish) article.summaryTr else article.summaryEn
    val publishedDate = remember(article.publishedAt, dateStrings) {
        article.publishedAt.toUserFriendlyDate(dateStrings.months, dateStrings.daysOfWeek)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (article.imageUrl != null) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(AppTheme.colors.cardHeaderBackground)
            )
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                color = AppTheme.colors.textPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.sp
            )

            Text(
                text = publishedDate,
                color = AppTheme.colors.textSecondary,
                fontSize = 13.sp
            )

            if (article.fighterNames.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    article.fighterNames.forEach { name ->
                        Text(
                            text = name,
                            color = AppTheme.colors.textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppTheme.colors.fightItemBackground)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Text(
                text = summary,
                color = AppTheme.colors.textPrimary,
                fontSize = 15.sp,
                lineHeight = 23.sp
            )

            Row(
                modifier = Modifier
                    .appClickable {
                        try {
                            uriHandler.openUri(article.sourceUrl)
                        } catch (e: Exception) {
                            // ignore
                        }
                    }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppTheme.strings.newsReadOnSource(article.sourceName),
                    color = AppTheme.colors.textSecondary,
                    fontSize = 13.sp,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    tint = AppTheme.colors.textSecondary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

private const val COMMENTS_HEADER_INDEX = 1
private const val LOAD_MORE_THRESHOLD = 3
private val REPLY_INDENT = 56.dp
private val THREAD_TEXT_START = 72.dp