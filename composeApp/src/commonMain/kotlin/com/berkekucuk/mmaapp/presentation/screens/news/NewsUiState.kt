package com.berkekucuk.mmaapp.presentation.screens.news

import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.domain.model.NewsArticle

data class NewsUiState(
    val isRefreshing: Boolean = false,
    val articles: List<NewsArticle> = emptyList(),
    val error: AppError? = null,
    val currentPage: Int = 0,
    val canGoNext: Boolean = true,
)
