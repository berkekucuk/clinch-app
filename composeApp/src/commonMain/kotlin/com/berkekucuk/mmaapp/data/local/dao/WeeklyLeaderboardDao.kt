package com.berkekucuk.mmaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.berkekucuk.mmaapp.data.local.entity.WeeklyLeaderboardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklyLeaderboardDao {

    @Query("SELECT * FROM weekly_leaderboard WHERE user_id NOT IN (SELECT blocked_user_id FROM blocked_users WHERE blocker_user_id = :currentUserId) ORDER BY weekly_points DESC, created_at ASC LIMIT 50")
    fun getWeeklyLeaderboard(currentUserId: String): Flow<List<WeeklyLeaderboardEntity>>

    @Upsert
    suspend fun upsertWeeklyLeaderboard(users: List<WeeklyLeaderboardEntity>)

    @Query("DELETE FROM weekly_leaderboard")
    suspend fun clearWeeklyLeaderboard()

    @Transaction
    suspend fun replaceWeeklyLeaderboard(users: List<WeeklyLeaderboardEntity>) {
        clearWeeklyLeaderboard()
        if (users.isNotEmpty()) {
            upsertWeeklyLeaderboard(users)
        }
    }
}