package com.berkekucuk.mmaapp.domain.repository

import com.berkekucuk.mmaapp.domain.model.EventComment
import kotlinx.coroutines.flow.Flow

interface EventCommentRepository {
    fun getComments(eventId: String): Flow<List<EventComment>>
    suspend fun refreshComments(eventId: String, force: Boolean = false): Result<Boolean>
    suspend fun loadOlderComments(eventId: String): Result<Boolean>
    suspend fun addComment(eventId: String, content: String): Result<Unit>
    suspend fun deleteComment(eventId: String, commentId: String): Result<Unit>
    suspend fun banUser(userId: String, eventId: String?): Result<Unit>
    suspend fun unbanUser(userId: String, eventId: String?): Result<Unit>
    suspend fun listenForCommentChanges(eventId: String): Result<Unit>
}