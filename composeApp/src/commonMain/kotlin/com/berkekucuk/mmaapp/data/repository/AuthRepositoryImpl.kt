package com.berkekucuk.mmaapp.data.repository

import com.berkekucuk.mmaapp.data.remote.datasource.DeviceTokenRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.fcm.DeviceTokenProvider
import com.berkekucuk.mmaapp.domain.model.AuthState
import com.berkekucuk.mmaapp.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
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
                .filter { it !is SessionStatus.Initializing }
                .collect {
                    registerDeviceToken()
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

    private suspend fun registerDeviceToken(): Result<Unit> {
        return try {
            val token = deviceTokenProvider.getToken() ?: return Result.success(Unit)
            deviceTokenRemoteDataSource.upsertToken(token, deviceTokenProvider.platform)
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            supabaseClient.auth.signOut()
            registerDeviceToken().getOrThrow()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
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
