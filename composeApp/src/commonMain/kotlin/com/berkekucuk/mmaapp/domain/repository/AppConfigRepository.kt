package com.berkekucuk.mmaapp.domain.repository

import com.berkekucuk.mmaapp.domain.model.AppConfig
import kotlinx.coroutines.flow.Flow

interface AppConfigRepository {
    fun getConfig(key: String): Flow<AppConfig?>
    suspend fun syncConfig(key: String): Result<Unit>
}
