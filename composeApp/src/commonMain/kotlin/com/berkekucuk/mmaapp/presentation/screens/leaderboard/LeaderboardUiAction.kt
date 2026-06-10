package com.berkekucuk.mmaapp.presentation.screens.leaderboard

sealed interface LeaderboardUiAction {
    data object OnBackClicked : LeaderboardUiAction
    data class OnUserClicked(val userId: String) : LeaderboardUiAction
    data object OnRefresh : LeaderboardUiAction
    data object OnErrorShown : LeaderboardUiAction
    data object OnNextPage : LeaderboardUiAction
    data object OnPreviousPage : LeaderboardUiAction
}
