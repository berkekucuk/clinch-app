package com.berkekucuk.mmaapp.data.repository

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
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

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
            .flowOn(Dispatchers.IO)
    }

    override fun getUserProfile(userId: String): Flow<UserProfile?> {
        return dao.getUserProfile(userId)
            .map { it?.toDomain() }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun syncUser(userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                if (!rateLimiter.shouldFetch(syncUserKey(userId))) {
                    return@runCatching
                }
                val remoteUser = remoteDataSource.fetchUser(userId)
                dao.upsertUsers(listOf(remoteUser.toEntity()))
            }.onFailure {
                if (it is CancellationException) throw it
                rateLimiter.reset(syncUserKey(userId))
            }
        }
    }

    override suspend fun updateUser(userId: String, fullName: String, username: String, avatarUrl: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                remoteDataSource.updateUser(userId, fullName, username, avatarUrl)
                dao.updateUser(userId, fullName, username, avatarUrl)
            }.onFailure {
                if (it is CancellationException) throw it
            }
        }
    }

    override suspend fun uploadAvatar(userId: String, imageBytes: ByteArray): Result<String> {
        return withContext(Dispatchers.IO) {
            runCatching {
                remoteDataSource.uploadAvatar(userId, imageBytes)
            }.onFailure {
                if (it is CancellationException) throw it
            }
        }
    }

    override suspend fun deleteUser(userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                remoteDataSource.deleteUser(userId)
                dao.deleteUser(userId)
            }.onFailure {
                if (it is CancellationException) throw it
            }
        }
    }

    override fun getBlockedUsers(currentUserId: String): Flow<List<User>> {
        return dao.getBlockedUsers(currentUserId)
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun reportUser(reporterId: String, reportedId: String, reason: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                remoteDataSource.reportUser(reporterId, reportedId, reason)
            }.onFailure {
                if (it is CancellationException) throw it
            }
        }
    }

    override suspend fun blockUser(blockerUserId: String, blockedUserId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                dao.upsertBlockedUser(BlockedUserEntity(blockerUserId, blockedUserId))
            }.onFailure {
                if (it is CancellationException) throw it
            }
        }
    }

    override suspend fun unblockUser(blockerUserId: String, blockedUserId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                dao.unblockUser(blockerUserId, blockedUserId)
            }.onFailure {
                if (it is CancellationException) throw it
            }
        }
    }

    override suspend fun searchUsers(query: String): Result<List<User>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                remoteDataSource.searchUsers(query)
                    .map { it.toDomain() }
            }.onFailure {
                if (it is CancellationException) throw it
            }
        }
    }
}