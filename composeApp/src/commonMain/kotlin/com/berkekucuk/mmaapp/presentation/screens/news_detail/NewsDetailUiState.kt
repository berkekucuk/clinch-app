package com.berkekucuk.mmaapp.presentation.screens.news_detail

import com.berkekucuk.mmaapp.domain.model.NewsArticle

data class NewsDetailUiState(
    val isRefreshing: Boolean = false,
    val article: NewsArticle? = null,
)
