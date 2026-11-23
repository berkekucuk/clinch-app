package com.berkekucuk.mmaapp.data.mapper

import com.berkekucuk.mmaapp.data.local.entity.NewsArticleEntity
import com.berkekucuk.mmaapp.data.remote.dto.NewsArticleDto
import com.berkekucuk.mmaapp.domain.model.NewsArticle

fun NewsArticleDto.toEntity(): NewsArticleEntity {
    return NewsArticleEntity(
        id = id,
        sourceUrl = sourceUrl,
        sourceName = sourceName,
        titleTr = titleTr,
        summaryTr = summaryTr,
        titleEn = titleEn,
        summaryEn = summaryEn,
        category = category ?: "general",
        fighterNames = fighterNames.orEmpty(),
        publishedAt = publishedAt,
        imageUrl = imageUrl?.takeIf { it.isNotBlank() },
        commentCount = commentCount,
    )
}

fun NewsArticleEntity.toDomain(): NewsArticle {
    return NewsArticle(
        id = id,
        sourceUrl = sourceUrl,
        sourceName = sourceName,
        titleTr = titleTr,
        summaryTr = summaryTr,
        titleEn = titleEn,
        summaryEn = summaryEn,
        category = category,
        fighterNames = fighterNames,
        publishedAt = publishedAt,
        imageUrl = imageUrl,
        commentCount = commentCount,
    )
}
