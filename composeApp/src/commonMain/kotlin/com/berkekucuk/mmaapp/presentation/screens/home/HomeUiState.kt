package com.berkekucuk.mmaapp.presentation.screens.home

import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.domain.model.AppUpdateStatus
import com.berkekucuk.mmaapp.domain.model.Event

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshingUpcomingTab: Boolean = false,
    val isRefreshingCompletedTab: Boolean = false,
    val selectedYear: Int = 0,
    val availableYears: List<Int> = emptyList(),
    val upcomingEvents: List<Event> = emptyList(),
    val completedEvents: List<Event> = emptyList(),
    val error: AppError? = null,
    val updateStatus: AppUpdateStatus? = null,
)

