package com.berkekucuk.mmaapp.domain.repository

import com.berkekucuk.mmaapp.domain.model.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authState: Flow<AuthState>
    suspend fun signOut(): Result<Unit>
    suspend fun getAuthenticatedUserId(): String?
    suspend fun getAuthenticatedUserEmail(): String?
}