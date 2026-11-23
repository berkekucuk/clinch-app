package com.berkekucuk.mmaapp.presentation.screens.news_edit

sealed interface NewsEditUiAction {
    data object OnBackClicked : NewsEditUiAction
    data class OnTitleTrChanged(val text: String) : NewsEditUiAction
    data class OnSummaryTrChanged(val text: String) : NewsEditUiAction
    data class OnTitleEnChanged(val text: String) : NewsEditUiAction
    data class OnSummaryEnChanged(val text: String) : NewsEditUiAction
    data object OnSaveClicked : NewsEditUiAction
    data object OnMessageShown : NewsEditUiAction
}
