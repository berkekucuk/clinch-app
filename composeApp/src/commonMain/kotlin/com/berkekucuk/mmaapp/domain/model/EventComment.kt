package com.berkekucuk.mmaapp.domain.model

import androidx.compose.runtime.Immutable
import kotlin.time.Instant

@Immutable
data class EventComment(
    val id: String,
    val eventId: String,
    val userId: String,
    val content: String,
    val createdAt: Instant,
    val username: String,
    val avatarUrl: String,
)
