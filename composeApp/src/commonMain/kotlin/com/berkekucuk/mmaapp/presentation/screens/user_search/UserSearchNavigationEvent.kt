package com.berkekucuk.mmaapp.presentation.screens.user_search

sealed interface UserSearchNavigationEvent {
    data class ToUserProfile(val userId: String) : UserSearchNavigationEvent
    data object Back : UserSearchNavigationEvent
}
