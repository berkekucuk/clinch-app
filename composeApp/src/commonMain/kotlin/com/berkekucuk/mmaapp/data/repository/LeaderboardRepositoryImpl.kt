package com.berkekucuk.mmaapp.data.repository

import com.berkekucuk.mmaapp.core.utils.RateLimiter
import com.berkekucuk.mmaapp.data.local.dao.UserDao
import com.berkekucuk.mmaapp.data.local.dao.WeeklyLeaderboardDao
import com.berkekucuk.mmaapp.data.mapper.toDomain
import com.berkekucuk.mmaapp.data.mapper.toEntity
import com.berkekucuk.mmaapp.data.mapper.toDomainUser
import com.berkekucuk.mmaapp.data.remote.datasource.LeaderboardRemoteDataSource
import com.berkekucuk.mmaapp.domain.model.User
import com.berkekucuk.mmaapp.domain.repository.LeaderboardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class LeaderboardRepositoryImpl(
    private val remoteDataSource: LeaderboardRemoteDataSource,
    private val userDao: UserDao,
    private val weeklyLeaderboardDao: WeeklyLeaderboardDao,
    private val rateLimiter: RateLimiter
) : LeaderboardRepository {

    private fun syncUsersKey(limit: Int, offset: Int) = "sync_users_limit_${limit}_offset_${offset}"
    private fun syncWeeklyLeaderboardKey(eventId: String) = "sync_weekly_${eventId}"

    override fun getLeaderboard(limit: Int, offset: Int, currentUserId: String): Flow<List<User>> {
        return userDao.getLeaderboard(limit, offset, currentUserId)
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    override fun getWeeklyLeaderboard(eventId: String, currentUserId: String): Flow<List<User>> {
        return weeklyLeaderboardDao.getWeeklyLeaderboard(eventId, currentUserId)
            .map { entities -> entities.map { it.toDomainUser() } }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun syncLeaderboard(limit: Int, offset: Int, currentUserId: String?): Result<Unit> {
        val key = syncUsersKey(limit, offset)
        return withContext(Dispatchers.IO) {
            runCatching {
                if (!rateLimiter.shouldFetch(key)) {
                    return@runCatching
                }
                val remoteUsers = remoteDataSource.fetchLeaderboard(limit, offset)
                
                if (currentUserId != null && offset == 0) {
                    userDao.replaceUsers(users = remoteUsers.map { it.toEntity() }, currentUserId = currentUserId)
                } else {
                    userDao.upsertUsers(users = remoteUsers.map { it.toEntity() })
                }
            }.onFailure {
                if (it is CancellationException) throw it
                rateLimiter.reset(key)
            }
        }
    }

    override suspend fun syncWeeklyLeaderboard(eventId: String): Result<Unit> {
        val key = syncWeeklyLeaderboardKey(eventId)
        return withContext(Dispatchers.IO) {
            runCatching {
                if (!rateLimiter.shouldFetch(key)) {
                    return@runCatching
                }
                val remoteUsers = remoteDataSource.fetchWeeklyLeaderboard(eventId)
                weeklyLeaderboardDao.replaceWeeklyLeaderboard(
                    eventId = eventId,
                    users = remoteUsers.map { it.toEntity() }
                )
            }.onFailure {
                if (it is CancellationException) throw it
                rateLimiter.reset(key)
            }
        }
    }
}
