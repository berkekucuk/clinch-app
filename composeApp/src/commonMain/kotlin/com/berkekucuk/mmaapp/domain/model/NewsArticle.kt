package com.berkekucuk.mmaapp.domain.model

import androidx.compose.runtime.Immutable
import kotlin.time.Instant

@Immutable
data class NewsArticle(
    val id: String,
    val sourceUrl: String,
    val sourceName: String,
    val titleTr: String,
    val summaryTr: String,
    val titleEn: String,
    val summaryEn: String,
    val category: String,
    val fighterNames: List<String>,
    val publishedAt: Instant,
    val imageUrl: String?,
    val commentCount: Int = 0,
)
