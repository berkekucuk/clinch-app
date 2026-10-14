package com.berkekucuk.mmaapp.data.repository

import com.berkekucuk.mmaapp.core.utils.runSuspendCatching
import com.berkekucuk.mmaapp.core.utils.RateLimiter
import com.berkekucuk.mmaapp.data.local.dao.NotificationDao
import com.berkekucuk.mmaapp.data.local.entity.FightNotificationEntity
import com.berkekucuk.mmaapp.data.mapper.toEntity
import com.berkekucuk.mmaapp.data.remote.datasource.NotificationRemoteDataSource
import com.berkekucuk.mmaapp.domain.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class NotificationRepositoryImpl(
    private val dao: NotificationDao,
    private val remoteDataSource: NotificationRemoteDataSource,
    private val rateLimiter: RateLimiter
) : NotificationRepository {

    private fun syncKey(userId: String) = "sync_notifications_$userId"

    override fun getFightNotificationStatus(fightId: String, userId: String): Flow<Boolean> {
        return dao.getFightNotificationStatus(fightId, userId)
            .distinctUntilChanged()
    }

    override suspend fun syncFightNotifications(userId: String): Result<Unit> {
        return runSuspendCatching(Dispatchers.Default) {
            if (!rateLimiter.shouldFetch(syncKey(userId))) {
                return@runSuspendCatching
            }
            val remoteNotifications = remoteDataSource.fetchFightNotifications(userId)
            dao.upsertFightNotifications(remoteNotifications.map { it.toEntity() })
        }.onFailure {
            rateLimiter.reset(syncKey(userId))
        }
    }

    override suspend fun addFightNotification(fightId: String, userId: String, isAlarm: Boolean): Result<Unit> {
        return runSuspendCatching {
            remoteDataSource.upsertFightNotification(fightId, userId, isAlarm)
            dao.upsertFightNotification(FightNotificationEntity(fightId = fightId, userId = userId))
        }
    }

    override suspend fun removeFightNotification(fightId: String, userId: String): Result<Unit> {
        return runSuspendCatching {
            remoteDataSource.deleteFightNotification(fightId, userId)
            dao.deleteFightNotification(fightId, userId)
        }
    }
}
