package com.berkekucuk.mmaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.berkekucuk.mmaapp.data.local.entity.NewsArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    @Query("SELECT * FROM news_articles ORDER BY published_at DESC, id DESC LIMIT :limit OFFSET :offset")
    fun getArticles(limit: Int, offset: Int): Flow<List<NewsArticleEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM news_articles LIMIT 1 OFFSET :offset)")
    suspend fun hasArticles(offset: Int): Boolean

    @Query("SELECT * FROM news_articles WHERE id = :id")
    fun getArticle(id: String): Flow<NewsArticleEntity?>

    @Query("UPDATE news_articles SET comment_count = MAX(comment_count + :delta, 0) WHERE id = :id")
    suspend fun adjustCommentCount(id: String, delta: Int)

    @Upsert
    suspend fun upsertArticles(articles: List<NewsArticleEntity>)

    @Query("DELETE FROM news_articles WHERE id = :id")
    suspend fun deleteArticle(id: String)

    @Query("DELETE FROM news_articles")
    suspend fun deleteAll()

    @Query("DELETE FROM news_articles WHERE id NOT IN (:retainedIds)")
    suspend fun deleteArticlesExcept(retainedIds: List<String>)

    @Transaction
    suspend fun replaceArticles(articles: List<NewsArticleEntity>) {
        val newIds = articles.map { it.id }
        if (newIds.isEmpty()) {
            deleteAll()
        } else {
            deleteArticlesExcept(newIds)
            upsertArticles(articles)
        }
    }
}
