package com.berkekucuk.mmaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.berkekucuk.mmaapp.data.local.entity.FightEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FightDao {
    @Query("SELECT * FROM fights WHERE fight_id = :fightId")
    fun getFight(fightId: String): Flow<FightEntity?>

    @Upsert
    suspend fun upsertFights(fights: List<FightEntity>)

    @Query("DELETE FROM fights WHERE event_id = :eventId")
    suspend fun deleteFights(eventId: String)

    @Query("DELETE FROM fights WHERE event_id = :eventId AND fight_id NOT IN (:retainedIds)")
    suspend fun deleteFightsExcept(eventId: String, retainedIds: List<String>)

    @Transaction
    suspend fun replaceFights(eventsMap: Map<String, List<FightEntity>>) {
        eventsMap.forEach { (eventId, fights) ->
            val newIds = fights.map { it.fightId }
            if (newIds.isEmpty()) {
                deleteFights(eventId)
            } else {
                deleteFightsExcept(eventId, newIds)
                upsertFights(fights)
            }
        }
    }
}
