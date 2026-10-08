package com.berkekucuk.mmaapp.presentation.screens.menu

sealed interface MenuUiAction {
    data object OnProfileClicked : MenuUiAction
    data object OnProfileEditClicked : MenuUiAction
    data object OnSettingsClicked : MenuUiAction
    data object OnSignOutClicked : MenuUiAction
    data object OnLeaderboardClicked : MenuUiAction
    data object OnErrorShown : MenuUiAction
}