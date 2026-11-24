package com.berkekucuk.mmaapp.data.remote.datasource

import com.berkekucuk.mmaapp.data.remote.dto.FightNotificationDto

interface NotificationRemoteDataSource {
    suspend fun fetchFightNotifications(): List<FightNotificationDto>
    suspend fun upsertFightNotification(fightId: String, isAlarm: Boolean)
    suspend fun deleteFightNotification(fightId: String)
}
