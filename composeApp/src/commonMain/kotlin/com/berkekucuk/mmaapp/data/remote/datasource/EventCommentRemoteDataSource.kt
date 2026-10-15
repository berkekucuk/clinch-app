package com.berkekucuk.mmaapp.data.remote.datasource

import com.berkekucuk.mmaapp.data.remote.dto.EventCommentDto
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

interface EventCommentRemoteDataSource {
    suspend fun fetchComments(
        eventId: String,
        limit: Int,
        beforeCreatedAt: Instant? = null,
        beforeId: String? = null,
        afterCreatedAt: Instant? = null,
        afterId: String? = null,
    ): List<EventCommentDto>
    suspend fun addComment(eventId: String, content: String): EventCommentDto
    suspend fun deleteComment(commentId: String)
    fun observeComments(eventId: String): Flow<EventCommentRealtimeEvent>
}

sealed interface EventCommentRealtimeEvent {
    data object Subscribed : EventCommentRealtimeEvent
    data class Added(val comment: EventCommentDto) : EventCommentRealtimeEvent
    data class Deleted(val commentId: String) : EventCommentRealtimeEvent
}
