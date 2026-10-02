package com.berkekucuk.mmaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.berkekucuk.mmaapp.data.local.entity.FightEntity
import com.berkekucuk.mmaapp.data.local.relation.FightWithStatsRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface FightDao {
    @Transaction
    @Query("SELECT * FROM fights WHERE fight_id = :fightId")
    fun getFight(fightId: String): Flow<FightWithStatsRelation?>

    @Upsert
    suspend fun upsertFights(fights: List<FightEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFightsIfNotExist(fights: List<FightEntity>)

    @Query("DELETE FROM fights WHERE event_id = :eventId")
    suspend fun deleteFights(eventId: String)

    @Query("DELETE FROM fights WHERE event_id = :eventId AND fight_id NOT IN (:retainedIds)")
    suspend fun deleteFightsExcept(eventId: String, retainedIds: List<String>)

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
