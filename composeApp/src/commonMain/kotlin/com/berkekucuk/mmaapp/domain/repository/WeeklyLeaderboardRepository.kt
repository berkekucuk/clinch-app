package com.berkekucuk.mmaapp.domain.repository

import com.berkekucuk.mmaapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface WeeklyLeaderboardRepository {
    fun getWeeklyLeaderboard(eventId: String, currentUserId: String): Flow<List<User>>
    suspend fun syncWeeklyLeaderboard(eventId: String): Result<Unit>
}
