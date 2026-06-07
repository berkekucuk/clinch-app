package com.berkekucuk.mmaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.berkekucuk.mmaapp.data.local.entity.PredictionEntity
import com.berkekucuk.mmaapp.data.local.relation.PredictionWithFightRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface PredictionDao {
    @Query("SELECT predicted_winner_id FROM predictions WHERE fight_id = :fightId AND user_id = :userId")
    fun getPredictedWinnerId(fightId: String, userId: String): Flow<String?>

    @Transaction
    @Query("""
        SELECT p.* FROM predictions p
        INNER JOIN fights f ON p.fight_id = f.fight_id
        WHERE p.user_id = :userId
        ORDER BY f.event_date DESC, f.fight_order DESC
        LIMIT :limit OFFSET :offset
    """)
    fun getPredictions(userId: String, limit: Int, offset: Int): Flow<List<PredictionWithFightRelation>>

    @Upsert
    suspend fun upsertPredictions(entities: List<PredictionEntity>)
}