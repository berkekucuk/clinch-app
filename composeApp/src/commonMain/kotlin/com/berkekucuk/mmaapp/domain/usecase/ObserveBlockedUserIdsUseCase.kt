package com.berkekucuk.mmaapp.domain.usecase

import com.berkekucuk.mmaapp.domain.repository.AuthRepository
import com.berkekucuk.mmaapp.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveBlockedUserIdsUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<Set<String>> {
        return authRepository.currentUserId.flatMapLatest { userId ->
            if (userId == null) flowOf(emptySet()) else userRepository.getBlockedUserIds(userId)
        }
    }
}
