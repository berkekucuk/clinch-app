package com.berkekucuk.mmaapp.data.mapper

import com.berkekucuk.mmaapp.data.local.entity.WeeklyLeaderboardEntity
import com.berkekucuk.mmaapp.data.remote.dto.WeeklyLeaderboardDto
import com.berkekucuk.mmaapp.domain.model.User


fun WeeklyLeaderboardDto.toEntity(): WeeklyLeaderboardEntity {
    return WeeklyLeaderboardEntity(
        eventId = eventId,
        userId = userId,
        username = username,
        fullName = fullName,
        avatarUrl = avatarUrl,
        weeklyPoints = weeklyPoints,
        createdAt = createdAt
    )
}

fun WeeklyLeaderboardEntity.toDomainUser(): User {
    return User(
        id = userId,
        username = username,
        fullName = fullName,
        avatarUrl = avatarUrl,
        totalPoints = weeklyPoints,
        createdAt = createdAt
    )
}