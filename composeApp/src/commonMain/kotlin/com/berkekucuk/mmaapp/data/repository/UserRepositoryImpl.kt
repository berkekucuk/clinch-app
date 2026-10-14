package com.berkekucuk.mmaapp.data.repository

import com.berkekucuk.mmaapp.core.utils.runSuspendCatching
import com.berkekucuk.mmaapp.core.utils.RateLimiter
import com.berkekucuk.mmaapp.data.local.dao.UserDao
import com.berkekucuk.mmaapp.data.local.entity.BlockedUserEntity
import com.berkekucuk.mmaapp.data.mapper.toDomain
import com.berkekucuk.mmaapp.data.mapper.toEntity
import com.berkekucuk.mmaapp.data.remote.datasource.UserRemoteDataSource
import com.berkekucuk.mmaapp.domain.model.User
import com.berkekucuk.mmaapp.domain.model.UserProfile
import com.berkekucuk.mmaapp.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource,
    private val dao: UserDao,
    private val rateLimiter: RateLimiter
) : UserRepository {

    private fun syncUserKey(userId: String) = "sync_user_$userId"

    override fun getUser(userId: String): Flow<User?> {
        return dao.getUser(userId)
            .map { entity -> entity?.toDomain() }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)
    }

    override fun getUserProfile(userId: String): Flow<UserProfile?> {
        return dao.getUserProfile(userId)
            .map { it?.toDomain() }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)
    }

    override suspend fun syncUser(userId: String): Result<Unit> {
        return runSuspendCatching(Dispatchers.Default) {
            if (!rateLimiter.shouldFetch(syncUserKey(userId))) {
                return@runSuspendCatching
            }
            val remoteUser = remoteDataSource.fetchUser(userId)
            dao.upsertUsers(listOf(remoteUser.toEntity()))
        }.onFailure {
            rateLimiter.reset(syncUserKey(userId))
        }
    }

    override suspend fun updateUser(userId: String, fullName: String, username: String, avatarUrl: String): Result<Unit> {
        return runSuspendCatching {
            remoteDataSource.updateUser(userId, fullName, username, avatarUrl)
            dao.updateUser(userId, fullName, username, avatarUrl)
        }
    }

    override suspend fun uploadAvatar(userId: String, imageBytes: ByteArray): Result<String> {
        return runSuspendCatching {
            remoteDataSource.uploadAvatar(userId, imageBytes)
        }
    }

    override suspend fun deleteUser(userId: String): Result<Unit> {
        return runSuspendCatching {
            remoteDataSource.deleteUser(userId)
            dao.deleteUser(userId)
        }
    }

    override fun getBlockedUsers(currentUserId: String): Flow<List<User>> {
        return dao.getBlockedUsers(currentUserId)
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)
    }

    override fun getBlockedUserIds(currentUserId: String): Flow<Set<String>> {
        return dao.getBlockedUserIds(currentUserId)
            .map { it.toSet() }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)
    }

    override suspend fun reportUser(reportedId: String, reason: String): Result<Unit> {
        return runSuspendCatching {
            remoteDataSource.reportUser(reason = reason, reportedId = reportedId)
        }
    }

    override suspend fun reportComment(commentId: String, reason: String): Result<Unit> {
        return runSuspendCatching {
            remoteDataSource.reportUser(reason = reason, commentId = commentId)
        }
    }

    override suspend fun blockUser(blockerUserId: String, blockedUserId: String): Result<Unit> {
        return runSuspendCatching {
            dao.upsertBlockedUser(BlockedUserEntity(blockerUserId, blockedUserId))
        }
    }

    override suspend fun unblockUser(blockerUserId: String, blockedUserId: String): Result<Unit> {
        return runSuspendCatching {
            dao.unblockUser(blockerUserId, blockedUserId)
        }
    }

    override suspend fun searchUsers(query: String): Result<List<User>> {
        return runSuspendCatching(Dispatchers.Default) {
            remoteDataSource.searchUsers(query)
                .map { it.toDomain() }
        }
    }
}