package com.berkekucuk.mmaapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlin.time.Instant

@Entity(
    tableName = "predictions",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PredictionEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "fight_id") val fightId: String,
    @ColumnInfo(name = "predicted_winner_id") val predictedWinnerId: String,
    @ColumnInfo(name = "points_earned") val pointsEarned: Int,
    @ColumnInfo(name = "is_correct") val isCorrect: Boolean?,
    @ColumnInfo(name = "locked_odds") val lockedOdds: Int?,
    @ColumnInfo(name = "predicted_method") val predictedMethod: String? = null,
    @ColumnInfo(name = "predicted_round") val predictedRound: Int? = null,
    @ColumnInfo(name = "created_at") val createdAt: Instant
)
