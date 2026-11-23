package com.berkekucuk.mmaapp.domain.repository

import com.berkekucuk.mmaapp.domain.model.NewsComment
import kotlinx.coroutines.flow.Flow

interface NewsCommentRepository {
    fun getComments(articleId: String): Flow<List<NewsComment>>
    suspend fun refreshComments(articleId: String, force: Boolean = false): Result<Boolean>
    suspend fun loadOlderComments(articleId: String): Result<Boolean>
    suspend fun addComment(articleId: String, content: String, parentId: String? = null): Result<Unit>
    suspend fun deleteComment(articleId: String, comment: NewsComment): Result<Int>
}
