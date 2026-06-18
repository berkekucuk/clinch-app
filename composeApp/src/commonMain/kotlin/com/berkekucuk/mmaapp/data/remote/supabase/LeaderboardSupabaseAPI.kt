package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.LeaderboardRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.UserDto
import com.berkekucuk.mmaapp.data.remote.dto.WeeklyLeaderboardDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

class LeaderboardSupabaseAPI(
    private val client: SupabaseClient
) : LeaderboardRemoteDataSource {

    override suspend fun fetchLeaderboard(limit: Int, offset: Int): List<UserDto> {
        return client.from("profile_view_v6").select {
            order("points", Order.DESCENDING)
            order("created_at", Order.ASCENDING)
            range(offset.toLong(), (offset + limit - 1).toLong())
        }.decodeList<UserDto>()
    }

    override suspend fun fetchWeeklyLeaderboard(): List<WeeklyLeaderboardDto> {
        return client.postgrest.rpc("get_weekly_leaderboard").decodeList<WeeklyLeaderboardDto>()
    }
}
