package com.berkekucuk.mmaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.berkekucuk.mmaapp.data.local.entity.AppConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppConfigDao {

    @Query("SELECT * FROM app_configs WHERE `key` = :key")
    fun getConfig(key: String): Flow<AppConfigEntity?>

    @Upsert
    suspend fun upsertConfig(config: AppConfigEntity)
}
