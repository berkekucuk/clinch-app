package com.berkekucuk.mmaapp.data.remote.datasource

import com.berkekucuk.mmaapp.data.remote.dto.NewsArticleDto

interface NewsRemoteDataSource {
    suspend fun fetchArticles(limit: Int, offset: Int): List<NewsArticleDto>
    suspend fun fetchArticle(id: String): NewsArticleDto?
}
