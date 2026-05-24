package com.berkekucuk.mmaapp.presentation.screens.profile

import com.berkekucuk.mmaapp.domain.enums.ReportReason

sealed interface ProfileUiAction {
    data object OnBackClicked : ProfileUiAction
    data object OnRefresh : ProfileUiAction
    data class OnInteractionListClicked(val type: String) : ProfileUiAction
    data class OnPredictionClicked(val fightId: String) : ProfileUiAction
    data object OnErrorDismissed : ProfileUiAction
    
    data object OnReportClicked : ProfileUiAction
    data class OnReportReasonChanged(val reason: ReportReason) : ProfileUiAction
    data object OnSubmitReport : ProfileUiAction
    data object OnDismissReportDialog : ProfileUiAction

    data object OnBlockClicked : ProfileUiAction
    data object OnDismissBlockDialog : ProfileUiAction
    data object OnConfirmBlock : ProfileUiAction
    
    data object OnNextPage : ProfileUiAction
    data object OnPreviousPage : ProfileUiAction
}
