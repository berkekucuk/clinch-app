package com.berkekucuk.mmaapp.presentation.screens.news_detail

sealed interface NewsDetailNavigationEvent {
    data object Back : NewsDetailNavigationEvent
    data class ToUserProfile(val userId: String) : NewsDetailNavigationEvent
}
