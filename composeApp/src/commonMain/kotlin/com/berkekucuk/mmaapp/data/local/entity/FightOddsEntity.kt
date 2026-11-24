package com.berkekucuk.mmaapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "fight_odds",
    primaryKeys = ["fight_id", "fighter_id", "method", "round"]
)
data class FightOddsEntity(
    @ColumnInfo(name = "fight_id") val fightId: String,
    @ColumnInfo(name = "fighter_id") val fighterId: String,
    @ColumnInfo(name = "method") val method: String,
    @ColumnInfo(name = "round") val round: Int,
    @ColumnInfo(name = "odds_value") val oddsValue: Int,
)
