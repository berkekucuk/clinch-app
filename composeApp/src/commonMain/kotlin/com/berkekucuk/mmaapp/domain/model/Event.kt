package com.berkekucuk.mmaapp.domain.model

import androidx.compose.runtime.Immutable
import com.berkekucuk.mmaapp.domain.enums.EventStatus
import kotlin.time.Instant

@Immutable
data class Event(
    val eventId: String,
    val name: String?,
    val status: EventStatus,
    val datetimeUtc: Instant?,
    val datetimeUtcMain: Instant?,
    val venue: String?,
    val location: String?,
    val eventYear: Int?,
    val fights: List<Fight>
) {
    val mainFight: Fight? = fights.firstOrNull { it.boutType.equals("Main Event", ignoreCase = true) }
            ?: fights.firstOrNull()

    val mainCardFights: List<Fight> = fights.filter {
            it.boutType.contains("Main Card", ignoreCase = true) ||
            it.boutType.contains("Main Event", ignoreCase = true) ||
            it.boutType.contains("Co-Main", ignoreCase = true) ||
            it.boutType.contains("Postlim", ignoreCase = true)
        }

    val prelimFights: List<Fight> = fights.filter {
            it.boutType.contains("Prelim", ignoreCase = true)
        }
}

