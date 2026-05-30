package com.berkekucuk.mmaapp.data.repository

import com.berkekucuk.mmaapp.core.utils.RateLimiter
import com.berkekucuk.mmaapp.data.local.dao.AppConfigDao
import com.berkekucuk.mmaapp.data.mapper.toDomain
import com.berkekucuk.mmaapp.data.mapper.toEntity
import com.berkekucuk.mmaapp.data.remote.datasource.AppConfigRemoteDataSource
import com.berkekucuk.mmaapp.domain.model.AppConfig
import com.berkekucuk.mmaapp.domain.repository.AppConfigRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class AppConfigRepositoryImpl(
    private val remoteDataSource: AppConfigRemoteDataSource,
    private val configDao: AppConfigDao,
    private val rateLimiter: RateLimiter
) : AppConfigRepository {

    private fun syncKey(key: String) = "sync_config_$key"

    override fun getConfig(key: String): Flow<AppConfig?> {
        return configDao.getConfig(key)
            .map { it?.toDomain() }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun syncConfig(key: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                if (!rateLimiter.shouldFetch(syncKey(key))) {
                    return@runCatching
                }

                val remoteConfig = remoteDataSource.fetchConfig(key)
                configDao.upsertConfig(remoteConfig.toEntity())
            }.onFailure {
                if (it is CancellationException) throw it
                rateLimiter.reset(syncKey(key))
            }
        }
    }
}
