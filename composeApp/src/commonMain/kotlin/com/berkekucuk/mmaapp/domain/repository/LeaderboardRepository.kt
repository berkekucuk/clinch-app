package com.berkekucuk.mmaapp.domain.repository

import com.berkekucuk.mmaapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface LeaderboardRepository {
    fun getLeaderboard(limit: Int, offset: Int, currentUserId: String): Flow<List<User>>
    fun getWeeklyLeaderboard(eventId: String, currentUserId: String): Flow<List<User>>
    suspend fun syncLeaderboard(limit: Int, offset: Int, currentUserId: String? = null): Result<Unit>
    suspend fun syncWeeklyLeaderboard(eventId: String): Result<Unit>
}
