package com.berkekucuk.mmaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.berkekucuk.mmaapp.data.local.entity.FightEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FightDao {
    @Query("SELECT * FROM fights WHERE fight_id = :fightId")
    fun getFight(fightId: String): Flow<FightEntity?>

    @Upsert
    suspend fun upsertFights(fights: List<FightEntity>)

    @Query("DELETE FROM fights WHERE event_id IN (:eventIds)")
    suspend fun deleteFightsByEventIds(eventIds: List<String>)
}
