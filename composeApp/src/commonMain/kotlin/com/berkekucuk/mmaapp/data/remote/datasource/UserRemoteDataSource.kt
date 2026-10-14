package com.berkekucuk.mmaapp.data.remote.datasource

import com.berkekucuk.mmaapp.data.remote.dto.UserDto

interface UserRemoteDataSource {

    suspend fun fetchUser(userId: String): UserDto
    suspend fun updateUser(userId: String, fullName: String, username: String, avatarUrl: String)
    suspend fun uploadAvatar(userId: String, imageBytes: ByteArray): String
    suspend fun deleteUser(userId: String)
    suspend fun reportUser(reason: String, reportedId: String? = null, commentId: String? = null)
    suspend fun searchUsers(query: String): List<UserDto>
}
