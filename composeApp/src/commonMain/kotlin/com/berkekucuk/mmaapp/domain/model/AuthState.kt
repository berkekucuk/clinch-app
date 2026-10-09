package com.berkekucuk.mmaapp.domain.model

sealed interface AuthState {
    data object Loading : AuthState
    data class Authenticated(
        val userId: String,
        val email: String?,
        val isAdmin: Boolean = false,
    ) : AuthState
    data object Unauthenticated : AuthState
}