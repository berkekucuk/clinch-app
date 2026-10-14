package com.berkekucuk.mmaapp.presentation.screens.event_comments

import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.domain.enums.BanAction
import com.berkekucuk.mmaapp.domain.enums.BanScope
import com.berkekucuk.mmaapp.domain.enums.CommentsSuccessMessage
import com.berkekucuk.mmaapp.domain.enums.ReportReason
import com.berkekucuk.mmaapp.domain.model.EventComment

data class EventCommentsUiState(
    val eventName: String? = null,
    val comments: List<EventComment> = emptyList(),
    val currentUserId: String? = null,
    val canModerate: Boolean = false,
    val isLoading: Boolean = true,
    val isLoadingOlderComments: Boolean = false,
    val hasOlderComments: Boolean = true,
    val commentInput: String = "",
    val isSending: Boolean = false,
    val pendingDeleteCommentId: String? = null,
    val pendingReportCommentId: String? = null,
    val selectedReportReason: ReportReason? = null,
    val isReporting: Boolean = false,
    val pendingBanUserId: String? = null,
    val pendingBanAction: BanAction? = null,
    val selectedBanScope: BanScope = BanScope.THIS_EVENT,
    val isSubmittingBan: Boolean = false,
    val successMessage: CommentsSuccessMessage? = null,
    val scrollToLatestTrigger: Int = 0,
    val error: AppError? = null,
)
