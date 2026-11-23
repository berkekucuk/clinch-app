package com.berkekucuk.mmaapp.presentation.screens.news_detail

import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.domain.enums.ReportReason
import com.berkekucuk.mmaapp.domain.model.NewsArticle
import com.berkekucuk.mmaapp.domain.model.NewsComment

data class NewsDetailUiState(
    val isRefreshing: Boolean = false,
    val article: NewsArticle? = null,
    val comments: List<NewsComment> = emptyList(),
    val replies: Map<String, List<NewsComment>> = emptyMap(),
    val expandedReplyParentIds: Set<String> = emptySet(),
    val replyingTo: NewsComment? = null,
    val focusInputTrigger: Int = 0,
    val currentUserId: String? = null,
    val canModerate: Boolean = false,
    val isLoadingComments: Boolean = true,
    val commentsLoadFailed: Boolean = false,
    val isLoadingOlderComments: Boolean = false,
    val hasOlderComments: Boolean = false,
    val commentInput: String = "",
    val isSending: Boolean = false,
    val pendingDeleteComment: NewsComment? = null,
    val pendingReportCommentId: String? = null,
    val selectedReportReason: ReportReason? = null,
    val isReporting: Boolean = false,
    val isReportSent: Boolean = false,
    val scrollToCommentsTrigger: Int = 0,
    val error: AppError? = null,
)
