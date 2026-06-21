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

    companion object {
        private const val SYNC_WEEKLY_KEY = "sync_weekly"
    }

    private fun syncUsersKey(limit: Int, offset: Int) = "sync_users_limit_${limit}_offset_${offset}"

    override fun getLeaderboard(limit: Int, offset: Int, currentUserId: String): Flow<List<User>> {
        return userDao.getLeaderboard(limit, offset, currentUserId)
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    override fun getWeeklyLeaderboard(currentUserId: String): Flow<List<User>> {
        return weeklyLeaderboardDao.getWeeklyLeaderboard(currentUserId)
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
                
                if (offset == 0) {
                    userDao.replaceUsers(users = remoteUsers.map { it.toEntity() }, currentUserId = currentUserId ?: "")
                } else {
                    userDao.upsertUsers(users = remoteUsers.map { it.toEntity() })
                }
            }.onFailure {
                if (it is CancellationException) throw it
                rateLimiter.reset(key)
            }
        }
    }

    override suspend fun syncWeeklyLeaderboard(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                if (!rateLimiter.shouldFetch(SYNC_WEEKLY_KEY)) {
                    return@runCatching
                }
                val remoteUsers = remoteDataSource.fetchWeeklyLeaderboard()
                weeklyLeaderboardDao.replaceWeeklyLeaderboard(
                    users = remoteUsers.map { it.toEntity() }
                )
            }.onFailure {
                if (it is CancellationException) throw it
                rateLimiter.reset(SYNC_WEEKLY_KEY)
            }
        }
    }
}
