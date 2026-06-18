package com.berkekucuk.mmaapp.data.remote.datasource

import com.berkekucuk.mmaapp.data.remote.dto.WeeklyLeaderboardDto

interface WeeklyLeaderboardRemoteDataSource {
    suspend fun fetchWeeklyLeaderboard(eventId: String): List<WeeklyLeaderboardDto>
}
