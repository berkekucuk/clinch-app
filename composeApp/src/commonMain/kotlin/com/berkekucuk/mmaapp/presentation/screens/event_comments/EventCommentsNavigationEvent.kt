package com.berkekucuk.mmaapp.presentation.screens.event_comments

sealed interface EventCommentsNavigationEvent {
    data object Back : EventCommentsNavigationEvent
    data class ToUserProfile(val userId: String) : EventCommentsNavigationEvent
}
