package com.berkekucuk.mmaapp.presentation.screens.news

sealed interface NewsUiAction {
    data object OnRefresh : NewsUiAction
    data object OnNextPage : NewsUiAction
    data object OnPreviousPage : NewsUiAction
    data class OnArticleClicked(val articleId: String) : NewsUiAction
}
