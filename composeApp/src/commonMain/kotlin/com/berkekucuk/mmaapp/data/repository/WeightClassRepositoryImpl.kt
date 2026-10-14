package com.berkekucuk.mmaapp.data.repository

import com.berkekucuk.mmaapp.core.utils.runSuspendCatching
import com.berkekucuk.mmaapp.core.utils.RateLimiter
import com.berkekucuk.mmaapp.data.local.dao.RankingDao
import com.berkekucuk.mmaapp.data.mapper.toDomain
import com.berkekucuk.mmaapp.data.mapper.toEntity
import com.berkekucuk.mmaapp.data.remote.datasource.WeightClassRemoteDataSource
import com.berkekucuk.mmaapp.domain.model.WeightClass
import com.berkekucuk.mmaapp.domain.repository.WeightClassRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class WeightClassRepositoryImpl(
    private val remoteDataSource: WeightClassRemoteDataSource,
    private val dao: RankingDao,
    private val rateLimiter: RateLimiter
) : WeightClassRepository {

    private companion object {
        const val KEY_SYNC_WEIGHT_CLASSES = "sync_weight_classes"
    }

    override fun getAllWeightClasses(): Flow<List<WeightClass>> {
        return dao.getAllWeightClasses()
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)
    }

    override fun getWeightClassById(weightClassId: String): Flow<WeightClass?> {
        return dao.getWeightClassById(weightClassId)
            .filterNotNull()
            .map { it.toDomain() }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)
    }

    override suspend fun hasData(): Boolean {
        return dao.getCount() > 0
    }

    override suspend fun syncWeightClasses(): Result<Unit> {
        return runSuspendCatching(Dispatchers.Default) {
            if (!rateLimiter.shouldFetch(KEY_SYNC_WEIGHT_CLASSES)) {
                return@runSuspendCatching
            }
            val dtos = remoteDataSource.fetchWeightClasses()
            if (dtos.isNotEmpty()) {
                dao.upsertWeightClasses(dtos.map { it.toEntity() })
            }
        }.onFailure {
            rateLimiter.reset(KEY_SYNC_WEIGHT_CLASSES)
        }
    }
}