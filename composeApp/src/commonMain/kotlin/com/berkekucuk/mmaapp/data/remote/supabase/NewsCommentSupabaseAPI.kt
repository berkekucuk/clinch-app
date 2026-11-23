package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.NewsCommentRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.NewsCommentDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.time.Instant

class NewsCommentSupabaseAPI(
    private val client: SupabaseClient
) : NewsCommentRemoteDataSource {

    override suspend fun fetchComments(
        articleId: String,
        limit: Int,
        beforeCreatedAt: Instant?,
        beforeId: String?,
    ): List<NewsCommentDto> {
        return client.postgrest.rpc(
            function = "get_news_comments",
            parameters = buildJsonObject {
                put("p_article_id", articleId)
                put("p_limit", limit)
                if (beforeCreatedAt != null && beforeId != null) {
                    put("p_before_created_at", beforeCreatedAt.toString())
                    put("p_before_id", beforeId)
                }
            }
        ).decodeList<NewsCommentDto>()
    }

    override suspend fun addComment(articleId: String, content: String, parentId: String?): NewsCommentDto {
        return client.postgrest.rpc(
            function = "add_news_comment",
            parameters = buildJsonObject {
                put("p_article_id", articleId)
                put("p_content", content)
                parentId?.let { put("p_parent_id", it) }
            }
        ).decodeList<NewsCommentDto>().first()
    }

    override suspend fun deleteComment(commentId: String) {
        client.postgrest.rpc(
            function = "delete_news_comment",
            parameters = buildJsonObject {
                put("p_comment_id", commentId)
            }
        )
    }

    override suspend fun reportComment(commentId: String, reason: String) {
        client.postgrest.rpc(
            function = "report_news_comment",
            parameters = buildJsonObject {
                put("p_comment_id", commentId)
                put("p_reason", reason)
            }
        )
    }
}
