package com.berkekucuk.mmaapp.data.repository

import com.berkekucuk.mmaapp.core.utils.RateLimiter
import com.berkekucuk.mmaapp.data.local.dao.WeeklyLeaderboardDao
import com.berkekucuk.mmaapp.data.mapper.toEntity
import com.berkekucuk.mmaapp.data.mapper.toDomainUser
import com.berkekucuk.mmaapp.data.remote.datasource.WeeklyLeaderboardRemoteDataSource
import com.berkekucuk.mmaapp.domain.model.User
import com.berkekucuk.mmaapp.domain.repository.WeeklyLeaderboardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class WeeklyLeaderboardRepositoryImpl(
    private val remoteDataSource: WeeklyLeaderboardRemoteDataSource,
    private val dao: WeeklyLeaderboardDao,
    private val rateLimiter: RateLimiter
) : WeeklyLeaderboardRepository {

    private fun syncWeeklyLeaderboardKey(eventId: String) = "sync_weekly_${eventId}"

    override fun getWeeklyLeaderboard(eventId: String, currentUserId: String): Flow<List<User>> {
        return dao.getWeeklyLeaderboard(eventId, currentUserId)
            .map { entities -> entities.map { it.toDomainUser() } }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun syncWeeklyLeaderboard(eventId: String): Result<Unit> {
        val key = syncWeeklyLeaderboardKey(eventId)
        return withContext(Dispatchers.IO) {
            runCatching {
                if (!rateLimiter.shouldFetch(key)) {
                    return@runCatching
                }
                val remoteUsers = remoteDataSource.fetchWeeklyLeaderboard(eventId)
                dao.replaceWeeklyLeaderboard(eventId = eventId, users = remoteUsers.map { it.toEntity() })
            }.onFailure {
                if (it is CancellationException) throw it
                rateLimiter.reset(key)
            }
        }
    }
}
