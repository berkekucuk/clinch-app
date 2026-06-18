package com.berkekucuk.mmaapp.presentation.screens.leaderboard

import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.domain.model.User

data class LeaderboardUiState(
    val isRefreshing: Boolean = false,
    val overallLeaderboard: List<User> = emptyList(),
    val weeklyLeaderboard: List<User> = emptyList(),
    val error: AppError? = null,
    val infoText: String? = null,
    val currentPage: Int = 0,
    val canGoNext: Boolean = false,
    val currentUserId: String? = null,
)
