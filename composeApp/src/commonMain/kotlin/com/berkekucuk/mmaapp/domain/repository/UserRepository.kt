package com.berkekucuk.mmaapp.domain.repository

import com.berkekucuk.mmaapp.domain.model.User
import com.berkekucuk.mmaapp.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(userId: String): Flow<User?>
    fun getUsers(limit: Int, offset: Int, currentUserId: String): Flow<List<User>>
    fun getUserProfile(userId: String): Flow<UserProfile?>
    suspend fun syncUser(userId: String): Result<Unit>
    suspend fun syncUsers(limit: Int, offset: Int, currentUserId: String? = null): Result<Unit>
    suspend fun updateUser(userId: String, fullName: String, username: String, avatarUrl: String): Result<Unit>
    suspend fun uploadAvatar(userId: String, imageBytes: ByteArray): Result<String>
    suspend fun deleteUser(userId: String): Result<Unit>
    suspend fun reportUser(reporterId: String, reportedId: String, reason: String): Result<Unit>
    fun getBlockedUsers(currentUserId: String): Flow<List<User>>
    suspend fun blockUser(blockerUserId: String, blockedUserId: String): Result<Unit>
    suspend fun unblockUser(blockerUserId: String, blockedUserId: String): Result<Unit>
}
