package com.berkekucuk.mmaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.berkekucuk.mmaapp.data.local.entity.FighterEntity
import com.berkekucuk.mmaapp.data.local.entity.FighterFightCrossRef
import com.berkekucuk.mmaapp.data.local.relation.FighterWithFightsRelation
import kotlinx.coroutines.flow.Flow
import androidx.room.Transaction
import androidx.room.Upsert

@Dao
interface FighterDao {

    @Transaction
    @Query("SELECT * FROM fighters WHERE fighter_id = :fighterId")
    fun getFighter(fighterId: String): Flow<FighterWithFightsRelation?>

    @Upsert
    suspend fun upsertFighters(fighters: List<FighterEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFighter(fighter: FighterEntity)

    @Upsert
    suspend fun upsertFighterFightCrossRefs(refs: List<FighterFightCrossRef>)

    @Query("DELETE FROM fighter_fights WHERE fighter_id = :fighterId")
    suspend fun deleteFighterFightCrossRefs(fighterId: String)
}
