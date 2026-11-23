package com.berkekucuk.mmaapp.presentation.screens.news_detail

import com.berkekucuk.mmaapp.domain.enums.ReportReason
import com.berkekucuk.mmaapp.domain.model.NewsComment

sealed interface NewsDetailUiAction {
    data object OnBackClicked : NewsDetailUiAction
    data object OnRefresh : NewsDetailUiAction
    data object OnEditArticleClicked : NewsDetailUiAction
    data object OnDeleteArticleClicked : NewsDetailUiAction
    data object OnConfirmDeleteArticle : NewsDetailUiAction
    data object OnDismissDeleteArticleDialog : NewsDetailUiAction
    data object OnLoadOlderComments : NewsDetailUiAction
    data class OnCommentInputChanged(val text: String) : NewsDetailUiAction
    data object OnSendClicked : NewsDetailUiAction
    data class OnUserClicked(val userId: String) : NewsDetailUiAction
    data class OnDeleteClicked(val comment: NewsComment) : NewsDetailUiAction
    data class OnReplyClicked(val comment: NewsComment) : NewsDetailUiAction
    data object OnCancelReply : NewsDetailUiAction
    data class OnToggleReplies(val parentId: String) : NewsDetailUiAction
    data object OnConfirmDelete : NewsDetailUiAction
    data object OnDismissDeleteDialog : NewsDetailUiAction
    data class OnReportClicked(val commentId: String) : NewsDetailUiAction
    data class OnReportReasonChanged(val reason: ReportReason) : NewsDetailUiAction
    data object OnSubmitReport : NewsDetailUiAction
    data object OnDismissReportDialog : NewsDetailUiAction
    data object OnMessageShown : NewsDetailUiAction
}
