package com.berkekucuk.mmaapp.data.mapper

import com.berkekucuk.mmaapp.data.local.entity.UserEntity
import com.berkekucuk.mmaapp.data.local.relation.UserProfileRelation
import com.berkekucuk.mmaapp.data.remote.dto.UserDto
import com.berkekucuk.mmaapp.domain.model.User
import com.berkekucuk.mmaapp.domain.model.UserProfile

fun UserDto.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        username = username,
        fullName = fullName,
        avatarUrl = avatarUrl,
        points = points ?: 0,
        createdAt = createdAt,
    )
}

fun UserDto.toDomain(): User {
    return User(
        id = id,
        username = username,
        fullName = fullName,
        avatarUrl = avatarUrl,
        points = points ?: 0,
        createdAt = createdAt,
    )
}

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        username = username,
        fullName = fullName,
        avatarUrl = avatarUrl,
        points = points,
        createdAt = createdAt,
    )
}

fun UserProfileRelation.toDomain(): UserProfile {
    return UserProfile(
        user = user.toDomain(),
        interactions = interactions.map { it.toDomain() }
    )
}