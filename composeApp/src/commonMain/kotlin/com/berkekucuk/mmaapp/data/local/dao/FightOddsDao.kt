package com.berkekucuk.mmaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.berkekucuk.mmaapp.data.local.entity.FightOddsEntity

@Dao
interface FightOddsDao {

    @Upsert
    suspend fun upsertOdds(odds: List<FightOddsEntity>)

    @Query("DELETE FROM fight_odds WHERE fight_id = :fightId")
    suspend fun deleteOddsForFight(fightId: String)

    @Query("DELETE FROM fight_odds WHERE fight_id IN (:fightIds) AND method = 'ANY'")
    suspend fun deleteMoneylines(fightIds: List<String>)

    @Transaction
    suspend fun replaceMoneylines(fightIds: List<String>, odds: List<FightOddsEntity>) {
        if (fightIds.isEmpty()) return
        deleteMoneylines(fightIds)
        if (odds.isNotEmpty()) {
            upsertOdds(odds)
        }
    }

    @Transaction
    suspend fun replaceOddsForFight(fightId: String, odds: List<FightOddsEntity>) {
        deleteOddsForFight(fightId)
        if (odds.isNotEmpty()) {
            upsertOdds(odds)
        }
    }
}
