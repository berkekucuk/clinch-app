package com.berkekucuk.mmaapp.presentation.screens.profile

import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.domain.enums.ReportReason
import com.berkekucuk.mmaapp.domain.model.UserProfile
import com.berkekucuk.mmaapp.domain.model.Prediction

data class ProfileUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val profile: UserProfile? = null,
    val predictions: List<Prediction> = emptyList(),
    val isOwner: Boolean = false,
    val showReportDialog: Boolean = false,
    val showBlockDialog: Boolean = false,
    val reportReason: ReportReason? = null,
    val isReporting: Boolean = false,
    val error: AppError? = null,
    val currentPage: Int = 0,
    val canGoNext: Boolean = true,
)
