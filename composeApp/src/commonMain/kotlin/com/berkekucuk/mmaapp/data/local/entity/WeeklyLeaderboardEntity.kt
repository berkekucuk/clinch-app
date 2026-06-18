package com.berkekucuk.mmaapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlin.time.Instant

@Entity(tableName = "weekly_leaderboard", primaryKeys = ["event_id", "user_id"])
data class WeeklyLeaderboardEntity(
    @ColumnInfo(name = "event_id") val eventId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    val username: String? = null,
    @ColumnInfo(name = "full_name") val fullName: String? = null,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String? = null,
    @ColumnInfo(name = "weekly_points") val weeklyPoints: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: Instant? = null
)
