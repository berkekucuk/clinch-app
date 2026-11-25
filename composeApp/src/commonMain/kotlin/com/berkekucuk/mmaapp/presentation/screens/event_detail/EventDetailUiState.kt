package com.berkekucuk.mmaapp.presentation.screens.event_detail

import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.domain.model.Event

data class EventDetailUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val event: Event? = null,
    val error: AppError? = null,
    val newCommentCount: Int = 0,
)