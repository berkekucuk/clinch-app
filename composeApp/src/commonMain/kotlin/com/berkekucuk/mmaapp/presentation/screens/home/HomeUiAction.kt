package com.berkekucuk.mmaapp.presentation.screens.home

sealed interface HomeUiAction {
    data class OnEventClicked(val eventId: String) : HomeUiAction
    data class OnYearSelected(val year: Int) : HomeUiAction
    data object OnRefreshUpcomingTab : HomeUiAction
    data object OnRefreshCompletedTab : HomeUiAction
    data object OnSearchClicked : HomeUiAction
    data object OnDismissFlexibleUpdate : HomeUiAction
}
