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

    override suspend fun updateArticle(
        id: String,
        titleTr: String,
        summaryTr: String,
        titleEn: String,
        summaryEn: String,
    ): NewsArticleDto {
        return client.postgrest.rpc(
            function = "update_news_article",
            parameters = buildJsonObject {
                put("p_id", id)
                put("p_title_tr", titleTr)
                put("p_summary_tr", summaryTr)
                put("p_title_en", titleEn)
                put("p_summary_en", summaryEn)
            }
        ).decodeList<NewsArticleDto>().first()
    }

    override suspend fun deleteArticle(id: String) {
        client.postgrest.rpc(
            function = "delete_news_article",
            parameters = buildJsonObject {
                put("p_id", id)
            }
        )
    }
}
