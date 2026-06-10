package com.berkekucuk.mmaapp.presentation.screens.leaderboard

import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.domain.model.User

data class LeaderboardUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val leaderboard: List<User> = emptyList(),
    val error: AppError? = null,
    val infoText: String? = null,
    val currentPage: Int = 0,
    val canGoNext: Boolean = false,
)
