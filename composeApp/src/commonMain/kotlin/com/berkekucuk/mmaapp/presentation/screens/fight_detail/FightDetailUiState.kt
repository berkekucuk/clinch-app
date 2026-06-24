package com.berkekucuk.mmaapp.presentation.screens.fight_detail

import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.domain.model.Fight
import com.berkekucuk.mmaapp.domain.model.Fighter

data class FightDetailUiState(
    val isRefreshing: Boolean = false,
    val fight: Fight? = null,
    val redFighter: Fighter? = null,
    val blueFighter: Fighter? = null,
    val error: AppError? = null,
    val isNotificationEnabled: Boolean = false,
    val showNotificationDialog: Boolean = false,
    val isSubmittingNotification: Boolean = false,
    val showPredictionBoard: Boolean = false,
    val showPredictionConfirmDialog: Boolean = false,
    val pendingPredictionFighterId: String? = null,
    val predictedWinnerId: String? = null,
    val isSubmittingPrediction: Boolean = false,
)