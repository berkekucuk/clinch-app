package com.berkekucuk.mmaapp.domain.usecase

import com.berkekucuk.mmaapp.domain.repository.NotificationRepository
import com.berkekucuk.mmaapp.domain.repository.PredictionRepository
import com.berkekucuk.mmaapp.domain.repository.UserRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SyncUserDataUseCase(
    private val userRepository: UserRepository,
    private val predictionRepository: PredictionRepository,
    private val notificationRepository: NotificationRepository,
) {
    suspend operator fun invoke(userId: String) {
        userRepository.syncUser(userId)
        coroutineScope {
            launch { predictionRepository.syncPredictions(userId, limit = 20, offset = 0) }
            launch { notificationRepository.syncFightNotifications(userId) }
        }
    }
}
