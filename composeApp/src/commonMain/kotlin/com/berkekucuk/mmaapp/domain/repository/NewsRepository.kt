package com.berkekucuk.mmaapp.domain.repository

import com.berkekucuk.mmaapp.domain.model.NewsArticle
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getArticles(limit: Int, offset: Int): Flow<List<NewsArticle>>
    suspend fun syncArticles(limit: Int, offset: Int): Result<Unit>
    suspend fun hasData(offset: Int): Boolean
    fun getArticle(id: String): Flow<NewsArticle?>
    suspend fun syncArticle(id: String): Result<Unit>
    suspend fun adjustCommentCount(id: String, delta: Int)
    suspend fun updateArticle(id: String, titleTr: String, summaryTr: String, titleEn: String, summaryEn: String): Result<Unit>
    suspend fun deleteArticle(id: String): Result<Unit>
}
