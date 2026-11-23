package com.berkekucuk.mmaapp.presentation.screens.news_edit

sealed interface NewsEditNavigationEvent {
    data object Back : NewsEditNavigationEvent
}
