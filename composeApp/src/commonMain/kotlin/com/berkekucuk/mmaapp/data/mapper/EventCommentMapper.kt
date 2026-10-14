package com.berkekucuk.mmaapp.data.mapper

import com.berkekucuk.mmaapp.data.remote.dto.EventCommentDto
import com.berkekucuk.mmaapp.domain.model.EventComment

fun EventCommentDto.toDomain(): EventComment = EventComment(
    id = id,
    eventId = eventId,
    userId = userId,
    content = content,
    createdAt = createdAt,
    username = username ?: "",
    avatarUrl = avatarUrl ?: "",
)
