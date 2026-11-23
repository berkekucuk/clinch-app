package com.berkekucuk.mmaapp.data.mapper

import com.berkekucuk.mmaapp.data.remote.dto.NewsCommentDto
import com.berkekucuk.mmaapp.domain.model.NewsComment

fun NewsCommentDto.toDomain(): NewsComment = NewsComment(
    id = id,
    articleId = articleId,
    userId = userId,
    content = content,
    createdAt = createdAt,
    username = username ?: "",
    avatarUrl = avatarUrl ?: "",
    parentId = parentId,
    replyToUserId = replyToUserId,
    replyToUsername = replyToUsername,
)
