package com.berkekucuk.mmaapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.Instant

@Entity(
    tableName = "news_articles",
    indices = [Index(value = ["published_at"])]
)
data class NewsArticleEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "source_url") val sourceUrl: String,
    @ColumnInfo(name = "source_name") val sourceName: String,
    @ColumnInfo(name = "title_tr") val titleTr: String,
    @ColumnInfo(name = "summary_tr") val summaryTr: String,
    @ColumnInfo(name = "title_en") val titleEn: String,
    @ColumnInfo(name = "summary_en") val summaryEn: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "fighter_names") val fighterNames: List<String>,
    @ColumnInfo(name = "published_at") val publishedAt: Instant,
    @ColumnInfo(name = "image_url") val imageUrl: String? = null,
)
