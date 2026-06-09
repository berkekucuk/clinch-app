package com.berkekucuk.mmaapp.core.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

private val Instant.localDateTime get() = toLocalDateTime(TimeZone.currentSystemDefault())

fun Instant.toUserFriendlyDate(
    months: List<String>,
    daysOfWeek: List<String>,
): String {
    val localDateTime = this.localDateTime
    val day = localDateTime.day.toString().padStart(2, '0')
    val month = months[localDateTime.month.number - 1]
    val year = localDateTime.year
    val dayOfWeek = daysOfWeek[localDateTime.dayOfWeek.ordinal]
    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')
    return "$day $month $year $dayOfWeek | $hour:$minute"
}

fun Instant.toYear(): String = localDateTime.year.toString()

fun Instant.toShortDate(months: List<String>): String {
    val ldt = localDateTime
    return "${months[ldt.month.number - 1]} ${ldt.day}"
}

fun Instant.toJoinedDate(months: List<String>): String {
    val ldt = localDateTime
    return "${ldt.day} ${months[ldt.month.number - 1]} ${ldt.year}"
}

fun calculateAgeAtDate(dateOfBirth: String?, referenceDate: Instant?): String? {
    if (dateOfBirth.isNullOrBlank() || referenceDate == null) return null
    return try {
        val birth = LocalDate.parse(dateOfBirth)
        val refDate = referenceDate.toLocalDateTime(TimeZone.UTC).date
        var age = refDate.year - birth.year
        if (refDate.month < birth.month ||
            (refDate.month == birth.month && refDate.day < birth.day)
        ) {
            age--
        }
        age.toString()
    } catch (e: Exception) {
        null
    }
}

fun formatDateOfBirth(dateOfBirth: String?, months: List<String>): String? {
    if (dateOfBirth.isNullOrBlank()) return null
    return try {
        val date = LocalDate.parse(dateOfBirth)
        val day = date.day.toString().padStart(2, '0')
        val month = months[date.month.number - 1]
        val year = date.year
        "$day $month $year"
    } catch (e: Exception) {
        null
    }
}
