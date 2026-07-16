package com.berkekucuk.mmaapp.presentation.screens.fight_detail

import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.domain.model.Fight

data class FightDetailUiState(
    val isRefreshing: Boolean = false,
    val fight: Fight? = null,
    val error: AppError? = null,

    // Notifications
    val isNotificationEnabled: Boolean = false,
    val showNotificationDialog: Boolean = false,
    val showNotificationSettingsDialog: Boolean = false,
    val showFullScreenIntentSettingsDialog: Boolean = false,
    val isSubmittingNotification: Boolean = false,

    // Predictions
    val showPredictionBoard: Boolean = false,
    val predictedWinnerId: String? = null,
    val showPredictionDialog: Boolean = false,
    val pendingPredictionFighterId: String? = null,
    val isSubmittingPrediction: Boolean = false,
)