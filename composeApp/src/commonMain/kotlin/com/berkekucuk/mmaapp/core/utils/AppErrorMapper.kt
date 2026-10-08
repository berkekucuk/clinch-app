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
                val message = e.message?.lowercase() ?: ""
                when {
                    message.contains("profiles_username_key") -> AppError.USERNAME_TAKEN
                    message.contains("unique_user_fighter_interaction") -> AppError.ALREADY_EXISTS
                    message.contains("unique_reporter_reported") -> AppError.ALREADY_REPORTED
                    message.contains("odds pending") -> AppError.ODDS_NOT_PUBLISHED
                    message.contains("event already over") -> AppError.EVENT_OVER
                    message.contains("fight already over") -> AppError.FIGHT_OVER
                    message.contains("result pending") -> AppError.FIGHT_PENDING
                    message.contains("not authenticated") || message.contains("unauthenticated") -> AppError.UNAUTHENTICATED
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
    UNKNOWN
}
