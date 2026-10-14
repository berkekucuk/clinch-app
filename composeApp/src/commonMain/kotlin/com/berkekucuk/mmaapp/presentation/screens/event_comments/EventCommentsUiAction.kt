package com.berkekucuk.mmaapp.presentation.screens.event_comments

import com.berkekucuk.mmaapp.domain.enums.BanScope
import com.berkekucuk.mmaapp.domain.enums.ReportReason

sealed interface EventCommentsUiAction {
    data object OnBackClicked : EventCommentsUiAction
    data object OnRetryClicked : EventCommentsUiAction
    data object OnLoadOlderComments : EventCommentsUiAction
    data class OnCommentInputChanged(val text: String) : EventCommentsUiAction
    data object OnSendClicked : EventCommentsUiAction
    data class OnUserClicked(val userId: String) : EventCommentsUiAction
    data class OnDeleteClicked(val commentId: String) : EventCommentsUiAction
    data object OnConfirmDelete : EventCommentsUiAction
    data object OnDismissDeleteDialog : EventCommentsUiAction
    data class OnReportClicked(val commentId: String) : EventCommentsUiAction
    data class OnReportReasonChanged(val reason: ReportReason) : EventCommentsUiAction
    data object OnSubmitReport : EventCommentsUiAction
    data object OnDismissReportDialog : EventCommentsUiAction
    data class OnBanClicked(val userId: String) : EventCommentsUiAction
    data class OnUnbanClicked(val userId: String) : EventCommentsUiAction
    data class OnBanScopeChanged(val scope: BanScope) : EventCommentsUiAction
    data object OnConfirmBan : EventCommentsUiAction
    data object OnDismissBanDialog : EventCommentsUiAction
    data object OnMessageShown : EventCommentsUiAction
}
