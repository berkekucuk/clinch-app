package com.berkekucuk.mmaapp.domain.repository

import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getFightNotificationStatus(fightId: String, userId: String): Flow<Boolean>
    suspend fun syncFightNotifications(userId: String): Result<Unit>
    suspend fun addFightNotification(fightId: String, userId: String, isAlarm: Boolean): Result<Unit>
    suspend fun removeFightNotification(fightId: String, userId: String): Result<Unit>
}
