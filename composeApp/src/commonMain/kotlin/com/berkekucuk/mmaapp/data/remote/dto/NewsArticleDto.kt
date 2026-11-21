package com.berkekucuk.mmaapp.data.remote.dto

import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsArticleDto(
    val id: String,
    @SerialName("source_url") val sourceUrl: String,
    @SerialName("source_name") val sourceName: String,
    @SerialName("ai_title_tr") val titleTr: String,
    @SerialName("ai_summary_tr") val summaryTr: String,
    @SerialName("ai_title_en") val titleEn: String,
    @SerialName("ai_summary_en") val summaryEn: String,
    val category: String? = null,
    @SerialName("fighter_names") val fighterNames: List<String>? = null,
    @SerialName("published_at") val publishedAt: Instant,
    @SerialName("image_url") val imageUrl: String? = null,
)
