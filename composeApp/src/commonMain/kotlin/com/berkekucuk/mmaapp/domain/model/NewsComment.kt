package com.berkekucuk.mmaapp.domain.model

import androidx.compose.runtime.Immutable
import kotlin.time.Instant

@Immutable
data class NewsComment(
    val id: String,
    val articleId: String,
    val userId: String,
    val content: String,
    val createdAt: Instant,
    val username: String,
    val avatarUrl: String,
    val parentId: String? = null,
    val replyToUserId: String? = null,
    val replyToUsername: String? = null,
)
