package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.WeeklyLeaderboardRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.WeeklyLeaderboardDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

class WeeklyLeaderboardSupabaseAPI(
    private val client: SupabaseClient
) : WeeklyLeaderboardRemoteDataSource {

    override suspend fun fetchWeeklyLeaderboard(eventId: String): List<WeeklyLeaderboardDto> {
        return client.postgrest["weekly_leaderboard_view"].select {
            filter {
                eq("event_id", eventId)
            }
            limit(50)
        }.decodeList<WeeklyLeaderboardDto>()
    }
}
