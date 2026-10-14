package com.berkekucuk.mmaapp.core.utils

import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.github.jan.supabase.auth.exception.AuthRestException

object AppErrorMapper {
    fun map(e: Throwable): AppError {
        return when (e) {
            is HttpRequestException -> AppError.NETWORK
            is AuthRestException -> AppError.UNAUTHENTICATED
            is PostgrestRestException -> {
                val message = e.error.lowercase()
                when {
                    message.contains("profiles_username_key") -> AppError.USERNAME_TAKEN
                    message.contains("unique_user_fighter_interaction") -> AppError.FIGHTER_ALREADY_IN_LIST
                    message.contains("unique_user_fight_prediction") -> AppError.ALREADY_PREDICTED
                    message.contains("already_reported") -> AppError.ALREADY_REPORTED
                    message.contains("rate_limited") -> AppError.COMMENT_RATE_LIMITED
                    message.contains("invalid_content") -> AppError.INVALID_COMMENT
                    message.contains("comment_not_found") -> AppError.COMMENT_NOT_FOUND
                    message.contains("not_authorized") -> AppError.NOT_AUTHORIZED
                    message.contains("comment_banned") -> AppError.COMMENT_BANNED
                    message.contains("ban_not_found") -> AppError.BAN_NOT_FOUND
                    message.contains("odds pending") -> AppError.ODDS_NOT_PUBLISHED
                    message.contains("event already over") -> AppError.EVENT_OVER
                    message.contains("fight already over") -> AppError.FIGHT_OVER
                    message.contains("result pending") -> AppError.FIGHT_PENDING
                    message.contains("not authenticated") || message.contains("not_authenticated") || message.contains("unauthenticated") || message.contains("permission denied for function") -> AppError.UNAUTHENTICATED
                    e.code == "23505" -> AppError.ALREADY_EXISTS
                    else -> AppError.SERVER_ERROR
                }
            }
            else -> AppError.UNKNOWN
        }
    }
}

enum class AppError {
    NETWORK,
    SERVER_ERROR,
    UNAUTHENTICATED,
    ALREADY_EXISTS,
    FIGHTER_ALREADY_IN_LIST,
    ALREADY_PREDICTED,
    USERNAME_TAKEN,
    EMPTY_FULLNAME,
    FULLNAME_TOO_SHORT,
    FULLNAME_TOO_LONG,
    EMPTY_USERNAME,
    INVALID_USERNAME,
    USERNAME_TOO_SHORT,
    USERNAME_TOO_LONG,
    ODDS_NOT_PUBLISHED,
    EVENT_OVER,
    FIGHT_OVER,
    FIGHT_PENDING,
    ALREADY_REPORTED,
    COMMENT_RATE_LIMITED,
    INVALID_COMMENT,
    COMMENT_NOT_FOUND,
    NOT_AUTHORIZED,
    COMMENT_BANNED,
    BAN_NOT_FOUND,
    UNKNOWN
}
