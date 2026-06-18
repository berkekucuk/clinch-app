package com.berkekucuk.mmaapp.data.remote.datasource

import com.berkekucuk.mmaapp.data.remote.dto.UserDto
import com.berkekucuk.mmaapp.data.remote.dto.WeeklyLeaderboardDto

interface LeaderboardRemoteDataSource {
    suspend fun fetchLeaderboard(limit: Int, offset: Int): List<UserDto>
    suspend fun fetchWeeklyLeaderboard(eventId: String): List<WeeklyLeaderboardDto>
}
