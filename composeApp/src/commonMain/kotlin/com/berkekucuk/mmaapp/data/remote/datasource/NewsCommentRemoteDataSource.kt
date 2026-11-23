package com.berkekucuk.mmaapp.data.remote.datasource

import com.berkekucuk.mmaapp.data.remote.dto.NewsCommentDto
import kotlin.time.Instant

interface NewsCommentRemoteDataSource {
    suspend fun fetchComments(
        articleId: String,
        limit: Int,
        beforeCreatedAt: Instant? = null,
        beforeId: String? = null,
    ): List<NewsCommentDto>
    suspend fun addComment(articleId: String, content: String, replyToCommentId: String?): NewsCommentDto
    suspend fun deleteComment(commentId: String)
}
