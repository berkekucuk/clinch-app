package com.berkekucuk.mmaapp.data.repository

import com.berkekucuk.mmaapp.data.remote.datasource.DeviceTokenRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.fcm.DeviceTokenProvider
import com.berkekucuk.mmaapp.domain.model.AuthState
import com.berkekucuk.mmaapp.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AuthRepositoryImpl(
    private val supabaseClient: SupabaseClient,
    private val deviceTokenRemoteDataSource: DeviceTokenRemoteDataSource,
    private val deviceTokenProvider: DeviceTokenProvider,
    private val scope: CoroutineScope
) : AuthRepository {

    init {
        scope.launch {
            supabaseClient.auth.sessionStatus
                .collect { status ->
                if (status is SessionStatus.Authenticated) {
                    registerDeviceToken(status.session.user?.id ?: "")
                }
            }
        }
    }

    override val authState: Flow<AuthState> = supabaseClient.auth.sessionStatus
        .map { status ->
            when (status) {
                is SessionStatus.Initializing -> AuthState.Loading
                is SessionStatus.Authenticated -> AuthState.Authenticated(
                    userId = status.session.user?.id ?: "",
                    email = status.session.user?.email
                )
                else -> AuthState.Unauthenticated
            }
        }

    private suspend fun registerDeviceToken(userId: String) {
        try {
            val token = deviceTokenProvider.getToken() ?: return
            deviceTokenRemoteDataSource.upsertToken(token, userId, deviceTokenProvider.platform)
        } catch (e: Exception) {
            println("Token registration failed: ${e.message}")
        }
    }

    override suspend fun signOut() {
        unregisterDeviceToken()
        try {
            supabaseClient.auth.signOut()
        } catch (e: Exception) {
            supabaseClient.auth.clearSession()
        }
    }

    private suspend fun unregisterDeviceToken() {
        try {
            val token = deviceTokenProvider.getToken() ?: return
            deviceTokenRemoteDataSource.deleteToken(token)
        } catch (e: Exception) {
            println("Token deletion failed: ${e.message}")
        }
    }

    override suspend fun getAuthenticatedUserId(): String? {
        val state = authState.first { it !is AuthState.Loading }
        return if (state is AuthState.Authenticated) state.userId else null
    }

    override suspend fun getAuthenticatedUserEmail(): String? {
        val state = authState.first { it !is AuthState.Loading }
        return if (state is AuthState.Authenticated) state.email else null
    }
}
