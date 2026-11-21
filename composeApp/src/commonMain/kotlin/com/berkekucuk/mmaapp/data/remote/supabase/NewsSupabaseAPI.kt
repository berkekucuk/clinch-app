package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.NewsRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.NewsArticleDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class NewsSupabaseAPI(
    private val client: SupabaseClient
) : NewsRemoteDataSource {

    override suspend fun fetchArticles(limit: Int, offset: Int): List<NewsArticleDto> {
        return client.postgrest.rpc(
            function = "get_news_articles",
            parameters = buildJsonObject {
                put("p_limit", limit)
                put("p_offset", offset)
            }
        ).decodeList<NewsArticleDto>()
    }

    override suspend fun fetchArticle(id: String): NewsArticleDto? {
        return client.postgrest.rpc(
            function = "get_news_article",
            parameters = buildJsonObject {
                put("p_id", id)
            }
        ).decodeList<NewsArticleDto>().firstOrNull()
    }
}
