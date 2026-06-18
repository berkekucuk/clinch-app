package com.berkekucuk.mmaapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Instant

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String? = null,
    @ColumnInfo(name = "full_name") val fullName: String? = null,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String? = null,
    @ColumnInfo(name = "points") val points: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: Instant? = null,
)
