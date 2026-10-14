package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.LeaderboardRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.UserDto
import com.berkekucuk.mmaapp.data.remote.dto.WeeklyLeaderboardDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class LeaderboardSupabaseAPI(
    private val client: SupabaseClient
) : LeaderboardRemoteDataSource {

    override suspend fun fetchLeaderboard(limit: Int, offset: Int): List<UserDto> {
        return client.postgrest.rpc(
            function = "get_leaderboard",
            parameters = buildJsonObject {
                put("p_limit", limit)
                put("p_offset", offset)
            }
        ).decodeList<UserDto>()
    }

    override suspend fun fetchWeeklyLeaderboard(): List<WeeklyLeaderboardDto> {
        return client.postgrest.rpc(
            function ="get_weekly_leaderboard"
        ).decodeList<WeeklyLeaderboardDto>()
    }
}
