package com.berkekucuk.mmaapp.presentation.screens.user_search

import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.domain.model.User

data class UserSearchUiState(
    val query: String = "",
    val results: List<User> = emptyList(),
    val error: AppError? = null,
)
