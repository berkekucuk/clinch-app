package com.berkekucuk.mmaapp.presentation.screens.fighter_detail

import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.domain.model.Fighter

data class FighterDetailUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val fighter: Fighter? = null,
    val error: AppError? = null,
    val isFavorite: Boolean = false,
    val favoriteInteractionId: String? = null,
)