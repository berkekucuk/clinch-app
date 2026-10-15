package com.berkekucuk.mmaapp.presentation.screens.event_comments

import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.domain.enums.ReportReason
import com.berkekucuk.mmaapp.domain.model.EventComment

data class EventCommentsUiState(
    val eventName: String? = null,
    val comments: List<EventComment> = emptyList(),
    val currentUserId: String? = null,
    val canModerate: Boolean = false,
    val isLoading: Boolean = true,
    val isLoadingOlderComments: Boolean = false,
    val hasOlderComments: Boolean = false,
    val commentInput: String = "",
    val isSending: Boolean = false,
    val pendingDeleteCommentId: String? = null,
    val pendingReportCommentId: String? = null,
    val selectedReportReason: ReportReason? = null,
    val isReporting: Boolean = false,
    val isReportSent: Boolean = false,
    val scrollToLatestTrigger: Int = 0,
    val error: AppError? = null,
)
