package com.berkekucuk.mmaapp.data.repository

import com.berkekucuk.mmaapp.core.utils.RateLimiter
import com.berkekucuk.mmaapp.core.utils.runSuspendCatching
import com.berkekucuk.mmaapp.data.mapper.toDomain
import com.berkekucuk.mmaapp.data.remote.datasource.EventCommentRealtimeEvent
import com.berkekucuk.mmaapp.data.remote.datasource.EventCommentRemoteDataSource
import com.berkekucuk.mmaapp.domain.model.EventComment
import com.berkekucuk.mmaapp.domain.repository.EventCommentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.Dispatchers

class EventCommentRepositoryImpl(
    private val remoteDataSource: EventCommentRemoteDataSource,
    private val rateLimiter: RateLimiter
) : EventCommentRepository {

    private companion object {
        const val PAGE_SIZE = 50
        const val MAX_PAGE_SIZE = 100
        val NEWEST_FIRST = compareByDescending<EventComment> { it.createdAt }.thenByDescending { it.id }
    }

    private val cache = MutableStateFlow<Map<String, List<EventComment>>>(emptyMap())
    private val fetchMutex = Mutex()

    private fun refreshKey(eventId: String) = "refresh_comments_$eventId"

    override fun getComments(eventId: String): Flow<List<EventComment>> {
        return cache
            .map { it[eventId].orEmpty() }
            .distinctUntilChanged()
    }

    override suspend fun refreshComments(eventId: String, force: Boolean): Result<Boolean> {
        val key = refreshKey(eventId)
        return runSuspendCatching(Dispatchers.Default) {
            val canFetch = rateLimiter.shouldFetch(key)
            val cachedComments = cache.value[eventId]
            if (!force && cachedComments != null && !canFetch) return@runSuspendCatching cachedComments.size >= PAGE_SIZE

            fetchMutex.withLock { fetchFirstPage(eventId) }
        }.onFailure {
            rateLimiter.reset(key)
        }
    }

    private suspend fun syncNewComments(eventId: String): Result<Unit> {
        return runSuspendCatching(Dispatchers.Default) {
            fetchMutex.withLock {
                val newestComment = cache.value[eventId]?.firstOrNull()
                if (newestComment == null) {
                    fetchFirstPage(eventId)
                    return@withLock
                }

                val newerComments = remoteDataSource.fetchComments(
                    eventId = eventId,
                    limit = MAX_PAGE_SIZE,
                    afterCreatedAt = newestComment.createdAt,
                    afterId = newestComment.id,
                )

                if (newerComments.size >= MAX_PAGE_SIZE) {
                    fetchFirstPage(eventId)
                } else {
                    addToCache(eventId, newerComments.map { it.toDomain() })
                }
            }
        }
    }

    override suspend fun loadOlderComments(eventId: String): Result<Boolean> {
        return runSuspendCatching(Dispatchers.Default) {
            fetchMutex.withLock {
                val oldestComment = cache.value[eventId]?.lastOrNull() ?: return@withLock false

                val olderComments = remoteDataSource.fetchComments(
                    eventId = eventId,
                    limit = PAGE_SIZE,
                    beforeCreatedAt = oldestComment.createdAt,
                    beforeId = oldestComment.id,
                )

                addToCache(eventId, olderComments.map { it.toDomain() })
                olderComments.size == PAGE_SIZE
            }
        }
    }

    override suspend fun addComment(eventId: String, content: String): Result<Unit> {
        return runSuspendCatching(Dispatchers.Default) {
            val comment = remoteDataSource.addComment(eventId, content)
            addToCache(eventId, listOf(comment.toDomain()))
        }
    }

    override suspend fun deleteComment(eventId: String, commentId: String): Result<Unit> {
        return runSuspendCatching {
            remoteDataSource.deleteComment(commentId)
            removeFromCache(eventId, commentId)
        }
    }

    override suspend fun banUser(userId: String, eventId: String?): Result<Unit> {
        return runSuspendCatching {
            remoteDataSource.banUser(userId, eventId)
        }
    }

    override suspend fun unbanUser(userId: String, eventId: String?): Result<Unit> {
        return runSuspendCatching {
            remoteDataSource.unbanUser(userId, eventId)
        }
    }

    override suspend fun listenForCommentChanges(eventId: String): Result<Unit> {
        return runSuspendCatching(Dispatchers.Default) {
            remoteDataSource.observeComments(eventId).collect { event ->
                when (event) {
                    is EventCommentRealtimeEvent.Subscribed -> syncNewComments(eventId)
                    is EventCommentRealtimeEvent.Added -> addToCache(eventId, listOf(event.comment.toDomain()))
                    is EventCommentRealtimeEvent.Deleted -> removeFromCache(eventId, event.commentId)
                }
            }
        }
    }

    private suspend fun fetchFirstPage(eventId: String): Boolean {
        val comments = remoteDataSource.fetchComments(eventId = eventId, limit = PAGE_SIZE)
        cache.update { it + (eventId to comments.map { comment -> comment.toDomain() }) }
        return comments.size == PAGE_SIZE
    }

    private fun addToCache(eventId: String, newComments: List<EventComment>) {
        cache.update { caches ->
            val comments = (newComments + caches[eventId].orEmpty())
                .distinctBy { it.id }
                .sortedWith(NEWEST_FIRST)
            caches + (eventId to comments)
        }
    }

    private fun removeFromCache(eventId: String, commentId: String) {
        cache.update { caches ->
            val comments = caches[eventId] ?: return@update caches
            caches + (eventId to comments.filterNot { it.id == commentId })
        }
    }
}
