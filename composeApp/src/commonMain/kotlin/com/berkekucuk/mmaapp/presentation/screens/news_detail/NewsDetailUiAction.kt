package com.berkekucuk.mmaapp.presentation.screens.news_detail

sealed interface NewsDetailUiAction {
    data object OnBackClicked : NewsDetailUiAction
    data object OnRefresh : NewsDetailUiAction
}
