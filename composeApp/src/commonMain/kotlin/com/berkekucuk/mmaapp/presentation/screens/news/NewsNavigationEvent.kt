package com.berkekucuk.mmaapp.presentation.screens.news

sealed interface NewsNavigationEvent {
    data class ToNewsDetail(val articleId: String) : NewsNavigationEvent
}
