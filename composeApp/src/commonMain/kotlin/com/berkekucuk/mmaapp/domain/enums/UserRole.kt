package com.berkekucuk.mmaapp.domain.enums

enum class UserRole {
    USER,
    MODERATOR,
    ADMIN;

    val canModerate: Boolean
        get() = this == MODERATOR || this == ADMIN

    companion object {
        fun fromString(role: String?): UserRole {
            return when (role?.lowercase()) {
                "admin" -> ADMIN
                "moderator" -> MODERATOR
                else -> USER
            }
        }
    }
}
