package com.berkekucuk.mmaapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "fight_stats",
    primaryKeys = ["fight_id", "fighter_id", "round"]
)
data class FightStatEntity(
    @ColumnInfo(name = "fight_id") val fightId: String,
    @ColumnInfo(name = "fighter_id") val fighterId: String,
    @ColumnInfo(name = "round") val round: Int,
    @ColumnInfo(name = "knockdowns") val knockdowns: Int?,
    @ColumnInfo(name = "sig_strikes_landed") val sigStrikesLanded: Int?,
    @ColumnInfo(name = "sig_strikes_attempted") val sigStrikesAttempted: Int?,
    @ColumnInfo(name = "total_strikes_landed") val totalStrikesLanded: Int?,
    @ColumnInfo(name = "total_strikes_attempted") val totalStrikesAttempted: Int?,
    @ColumnInfo(name = "takedowns_landed") val takedownsLanded: Int?,
    @ColumnInfo(name = "takedowns_attempted") val takedownsAttempted: Int?,
    @ColumnInfo(name = "submission_attempts") val submissionAttempts: Int?,
    @ColumnInfo(name = "reversals") val reversals: Int?,
    @ColumnInfo(name = "control_time_seconds") val controlTimeSeconds: Int?,
    @ColumnInfo(name = "head_landed") val headLanded: Int?,
    @ColumnInfo(name = "head_attempted") val headAttempted: Int?,
    @ColumnInfo(name = "body_landed") val bodyLanded: Int?,
    @ColumnInfo(name = "body_attempted") val bodyAttempted: Int?,
    @ColumnInfo(name = "leg_landed") val legLanded: Int?,
    @ColumnInfo(name = "leg_attempted") val legAttempted: Int?,
    @ColumnInfo(name = "distance_landed") val distanceLanded: Int?,
    @ColumnInfo(name = "distance_attempted") val distanceAttempted: Int?,
    @ColumnInfo(name = "clinch_landed") val clinchLanded: Int?,
    @ColumnInfo(name = "clinch_attempted") val clinchAttempted: Int?,
    @ColumnInfo(name = "ground_landed") val groundLanded: Int?,
    @ColumnInfo(name = "ground_attempted") val groundAttempted: Int?
)
