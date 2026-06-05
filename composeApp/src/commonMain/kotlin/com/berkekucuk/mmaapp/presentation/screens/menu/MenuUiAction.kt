package com.berkekucuk.mmaapp.presentation.screens.menu

import com.berkekucuk.mmaapp.core.utils.AppError

sealed interface MenuUiAction {
    data object OnProfileClicked : MenuUiAction
    data object OnProfileEditClicked : MenuUiAction
    data object OnSettingsClicked : MenuUiAction
    data object OnSignOutClicked : MenuUiAction
    data object OnLeaderboardClicked : MenuUiAction
    data class OnErrorOccurred(val error: AppError) : MenuUiAction
    data object OnErrorShown : MenuUiAction
}