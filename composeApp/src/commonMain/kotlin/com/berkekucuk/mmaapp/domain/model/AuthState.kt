package com.berkekucuk.mmaapp.domain.model

import com.berkekucuk.mmaapp.domain.enums.UserRole

sealed interface AuthState {
    data object Loading : AuthState
    data class Authenticated(
        val userId: String,
        val email: String?,
        val role: UserRole = UserRole.USER,
    ) : AuthState
    data object Unauthenticated : AuthState
}