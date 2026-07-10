package com.berkekucuk.mmaapp.presentation.screens.leaderboard

sealed interface LeaderboardNavigationEvent {
    data object Back : LeaderboardNavigationEvent
    data class ToUserProfile(val userId: String) : LeaderboardNavigationEvent
    data object ToUserSearch : LeaderboardNavigationEvent
}
