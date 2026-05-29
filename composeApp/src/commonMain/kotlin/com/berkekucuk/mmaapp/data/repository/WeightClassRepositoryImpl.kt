package com.berkekucuk.mmaapp.data.repository

import com.berkekucuk.mmaapp.core.utils.RateLimiter
import com.berkekucuk.mmaapp.data.local.dao.RankingDao
import com.berkekucuk.mmaapp.data.mapper.toDomain
import com.berkekucuk.mmaapp.data.mapper.toEntity
import com.berkekucuk.mmaapp.data.remote.datasource.WeightClassRemoteDataSource
import com.berkekucuk.mmaapp.domain.model.WeightClass
import com.berkekucuk.mmaapp.domain.repository.WeightClassRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class WeightClassRepositoryImpl(
    private val remoteDataSource: WeightClassRemoteDataSource,
    private val dao: RankingDao,
    private val rateLimiter: RateLimiter
) : WeightClassRepository {

    private companion object {
        private const val KEY_SYNC_WEIGHT_CLASSES = "sync_weight_classes"
    }

    override fun getAllWeightClasses(): Flow<List<WeightClass>> {
        return dao.getAllWeightClasses()
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    override fun getWeightClassById(weightClassId: String): Flow<WeightClass?> {
        return dao.getWeightClassById(weightClassId)
            .filterNotNull()
            .map { it.toDomain() }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun hasData(): Boolean {
        return withContext(Dispatchers.IO) { dao.getCount() > 0 }
    }

    override suspend fun syncWeightClasses(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                if (!rateLimiter.shouldFetch(KEY_SYNC_WEIGHT_CLASSES)) {
                    return@runCatching
                }
                val dtos = remoteDataSource.fetchWeightClasses()
                if (dtos.isNotEmpty()) {
                    dao.upsertWeightClasses(dtos.map { it.toEntity() })
                }
            }.onFailure {
                if (it is CancellationException) throw it
                rateLimiter.reset(KEY_SYNC_WEIGHT_CLASSES)
            }
        }
    }
}