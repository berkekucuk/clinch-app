package com.berkekucuk.mmaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.berkekucuk.mmaapp.data.local.entity.FightStatEntity

@Dao
interface FightStatDao {

    @Upsert
    suspend fun upsertStats(stats: List<FightStatEntity>)

    @Query("DELETE FROM fight_stats WHERE fight_id = :fightId")
    suspend fun deleteStatsForFight(fightId: String)

    @Query("DELETE FROM fight_stats WHERE fight_id = :fightId AND round NOT IN (:retainedRounds)")
    suspend fun deleteStatsForFightExcept(fightId: String, retainedRounds: List<Int>)

    suspend fun replaceStatsForFight(fightId: String, stats: List<FightStatEntity>) {
        val newRounds = stats.map { it.round }.distinct()
        if (newRounds.isEmpty()) {
            deleteStatsForFight(fightId)
        } else {
            deleteStatsForFightExcept(fightId, newRounds)
            upsertStats(stats)
        }
    }
}
