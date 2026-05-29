package com.berkekucuk.mmaapp.data.repository

import com.berkekucuk.mmaapp.core.utils.RateLimiter
import com.berkekucuk.mmaapp.data.local.dao.NotificationDao
import com.berkekucuk.mmaapp.data.local.entity.FightNotificationEntity
import com.berkekucuk.mmaapp.data.mapper.toEntity
import com.berkekucuk.mmaapp.data.remote.datasource.NotificationRemoteDataSource
import com.berkekucuk.mmaapp.domain.repository.NotificationRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class NotificationRepositoryImpl(
    private val dao: NotificationDao,
    private val remoteDataSource: NotificationRemoteDataSource,
    private val rateLimiter: RateLimiter
) : NotificationRepository {

    private fun syncKey(userId: String) = "sync_notifications_$userId"

    override fun getFightNotificationStatus(fightId: String, userId: String): Flow<Boolean> {
        return dao.getFightNotificationStatus(fightId, userId)
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun syncFightNotifications(userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                if (!rateLimiter.shouldFetch(syncKey(userId))) {
                    return@runCatching
                }
                val remoteNotifications = remoteDataSource.fetchFightNotifications(userId)
                dao.upsertFightNotifications(remoteNotifications.map { it.toEntity() })
            }.onFailure {
                if (it is CancellationException) throw it
                rateLimiter.reset(syncKey(userId))
            }
        }
    }

    override suspend fun addFightNotification(fightId: String, userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                remoteDataSource.upsertFightNotification(fightId, userId)
                dao.upsertFightNotification(FightNotificationEntity(fightId = fightId, userId = userId))
            }.onFailure {
                if (it is CancellationException) throw it
            }
        }
    }

    override suspend fun removeFightNotification(fightId: String, userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                remoteDataSource.deleteFightNotification(fightId, userId)
                dao.deleteFightNotification(fightId, userId)
            }.onFailure {
                if (it is CancellationException) throw it
            }
        }
    }
}
