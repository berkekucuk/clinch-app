package com.berkekucuk.mmaapp.data.repository

import com.berkekucuk.mmaapp.core.utils.RateLimiter
import com.berkekucuk.mmaapp.core.utils.runSuspendCatching
import com.berkekucuk.mmaapp.data.mapper.toDomain
import com.berkekucuk.mmaapp.data.remote.datasource.NewsCommentRemoteDataSource
import com.berkekucuk.mmaapp.domain.model.NewsComment
import com.berkekucuk.mmaapp.domain.repository.NewsCommentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.Dispatchers

class NewsCommentRepositoryImpl(
    private val remoteDataSource: NewsCommentRemoteDataSource,
    private val rateLimiter: RateLimiter
) : NewsCommentRepository {

    private companion object {
        const val PAGE_SIZE = 20
        val OLDEST_FIRST = compareBy<NewsComment> { it.createdAt }.thenBy { it.id }
    }

    private val cache = MutableStateFlow<Map<String, List<NewsComment>>>(emptyMap())
    private val fetchMutex = Mutex()

    private fun refreshKey(articleId: String) = "refresh_news_comments_$articleId"

    override fun getComments(articleId: String): Flow<List<NewsComment>> {
        return cache
            .map { it[articleId].orEmpty() }
            .distinctUntilChanged()
    }

    override suspend fun refreshComments(articleId: String, force: Boolean): Result<Boolean> {
        val key = refreshKey(articleId)
        return runSuspendCatching(Dispatchers.Default) {
            val canFetch = rateLimiter.shouldFetch(key)
            val cachedComments = cache.value[articleId]
            if (!force && cachedComments != null && !canFetch) return@runSuspendCatching cachedComments.topLevelCount() >= PAGE_SIZE

            fetchMutex.withLock { fetchFirstPage(articleId) }
        }.onFailure {
            rateLimiter.reset(key)
        }
    }

    override suspend fun loadOlderComments(articleId: String): Result<Boolean> {
        return runSuspendCatching(Dispatchers.Default) {
            fetchMutex.withLock {
                val oldestComment = cache.value[articleId]?.firstOrNull { it.parentId == null } ?: return@withLock false

                val olderComments = remoteDataSource.fetchComments(
                    articleId = articleId,
                    limit = PAGE_SIZE,
                    beforeCreatedAt = oldestComment.createdAt,
                    beforeId = oldestComment.id,
                ).map { it.toDomain() }

                addToCache(articleId, olderComments)
                olderComments.topLevelCount() == PAGE_SIZE
            }
        }
    }

    override suspend fun addComment(articleId: String, content: String, parentId: String?): Result<Unit> {
        return runSuspendCatching(Dispatchers.Default) {
            val comment = remoteDataSource.addComment(articleId, content, parentId)
            addToCache(articleId, listOf(comment.toDomain()))
        }
    }

    override suspend fun deleteComment(articleId: String, comment: NewsComment): Result<Int> {
        return runSuspendCatching {
            remoteDataSource.deleteComment(comment.id)
            removeFromCache(articleId, comment.id)
        }
    }

    private suspend fun fetchFirstPage(articleId: String): Boolean {
        val comments = remoteDataSource.fetchComments(articleId = articleId, limit = PAGE_SIZE)
        cache.update { it + (articleId to comments.map { comment -> comment.toDomain() }.sortedWith(OLDEST_FIRST)) }
        return comments.count { it.parentId == null } == PAGE_SIZE
    }

    private fun addToCache(articleId: String, newComments: List<NewsComment>) {
        cache.update { caches ->
            val comments = (caches[articleId].orEmpty() + newComments)
                .distinctBy { it.id }
                .sortedWith(OLDEST_FIRST)
            caches + (articleId to comments)
        }
    }

    private fun removeFromCache(articleId: String, commentId: String): Int {
        var removedCount = 0
        cache.update { caches ->
            val comments = caches[articleId] ?: return@update caches
            val remaining = comments.filterNot { it.id == commentId || it.parentId == commentId }
            removedCount = comments.size - remaining.size
            caches + (articleId to remaining)
        }
        return removedCount
    }

    private fun List<NewsComment>.topLevelCount(): Int = count { it.parentId == null }
}
