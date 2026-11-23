package com.berkekucuk.mmaapp.data.repository

import com.berkekucuk.mmaapp.core.utils.RateLimiter
import com.berkekucuk.mmaapp.core.utils.runSuspendCatching
import com.berkekucuk.mmaapp.data.local.dao.NewsDao
import com.berkekucuk.mmaapp.data.mapper.toDomain
import com.berkekucuk.mmaapp.data.mapper.toEntity
import com.berkekucuk.mmaapp.data.remote.datasource.NewsRemoteDataSource
import com.berkekucuk.mmaapp.domain.model.NewsArticle
import com.berkekucuk.mmaapp.domain.repository.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class NewsRepositoryImpl(
    private val remoteDataSource: NewsRemoteDataSource,
    private val dao: NewsDao,
    private val rateLimiter: RateLimiter
) : NewsRepository {

    private fun syncKey(limit: Int, offset: Int) = "sync_news_limit_${limit}_offset_${offset}"
    private fun articleSyncKey(id: String) = "sync_news_article_$id"

    override fun getArticles(limit: Int, offset: Int): Flow<List<NewsArticle>> {
        return dao.getArticles(limit, offset)
            .map { entities ->
                entities.map { it.toDomain() }
            }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)
    }

    override suspend fun syncArticles(limit: Int, offset: Int): Result<Unit> {
        val key = syncKey(limit, offset)
        return runSuspendCatching(Dispatchers.Default) {
            if (!rateLimiter.shouldFetch(key)) {
                return@runSuspendCatching
            }

            val remoteArticles = remoteDataSource.fetchArticles(limit, offset).map { it.toEntity() }

            if (offset == 0) {
                dao.replaceArticles(remoteArticles)
            } else if (remoteArticles.isNotEmpty()) {
                dao.upsertArticles(remoteArticles)
            }
        }.onFailure {
            rateLimiter.reset(key)
        }
    }

    override suspend fun hasData(offset: Int): Boolean {
        return dao.hasArticles(offset)
    }

    override fun getArticle(id: String): Flow<NewsArticle?> {
        return dao.getArticle(id)
            .map { it?.toDomain() }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)
    }

    override suspend fun adjustCommentCount(id: String, delta: Int) {
        runSuspendCatching(Dispatchers.Default) {
            dao.adjustCommentCount(id, delta)
        }
    }

    override suspend fun updateArticle(
        id: String,
        titleTr: String,
        summaryTr: String,
        titleEn: String,
        summaryEn: String,
    ): Result<Unit> {
        return runSuspendCatching(Dispatchers.Default) {
            val dto = remoteDataSource.updateArticle(id, titleTr, summaryTr, titleEn, summaryEn)
            dao.upsertArticles(listOf(dto.toEntity()))
        }
    }

    override suspend fun deleteArticle(id: String): Result<Unit> {
        return runSuspendCatching(Dispatchers.Default) {
            remoteDataSource.deleteArticle(id)
            dao.deleteArticle(id)
        }
    }

    override suspend fun syncArticle(id: String): Result<Unit> {
        val key = articleSyncKey(id)
        return runSuspendCatching(Dispatchers.Default) {
            if (!rateLimiter.shouldFetch(key)) {
                return@runSuspendCatching
            }
            val dto = remoteDataSource.fetchArticle(id) ?: return@runSuspendCatching
            dao.upsertArticles(listOf(dto.toEntity()))
        }.onFailure {
            rateLimiter.reset(key)
        }
    }
}
