package com.berkekucuk.mmaapp.presentation.screens.user_search

sealed interface UserSearchUiAction {
    data class OnQueryChanged(val query: String) : UserSearchUiAction
    data object OnClearQuery : UserSearchUiAction
    data class OnUserClicked(val userId: String) : UserSearchUiAction
    data object OnBackClicked : UserSearchUiAction
}
