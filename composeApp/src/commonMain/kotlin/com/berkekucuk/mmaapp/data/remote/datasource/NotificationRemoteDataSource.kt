package com.berkekucuk.mmaapp.data.remote.datasource

import com.berkekucuk.mmaapp.data.remote.dto.FightNotificationDto

interface NotificationRemoteDataSource {
    suspend fun fetchFightNotifications(userId: String): List<FightNotificationDto>
    suspend fun upsertFightNotification(fightId: String, userId: String)
    suspend fun deleteFightNotification(fightId: String, userId: String)
}
