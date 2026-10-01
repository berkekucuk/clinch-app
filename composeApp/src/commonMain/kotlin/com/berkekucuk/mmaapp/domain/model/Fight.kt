package com.berkekucuk.mmaapp.domain.model

import androidx.compose.runtime.Immutable
import kotlin.time.Instant

@Immutable
data class Fight(
    val fightId: String,
    val eventId: String,
    val eventName: String?,
    val eventDate: Instant?,
    val methodType: String?,
    val methodDetail: String?,
    val roundSummary: String?,
    val boutType: String,
    val weightClassLbs: Int?,
    val weightClassId: String,
    val roundsFormat: String,
    val fightOrder: Int,
    val titleType: String?,
    val referee: String?,
    val bonuses: List<String> = emptyList(),
    val participants: List<Participant> = emptyList(),
    val stats: List<FightStat> = emptyList()
) {
    val redCorner: Participant? = participants.find { it.isRedCorner }

    val blueCorner: Participant? = participants.find { !it.isRedCorner }

    val isCancelledOrFizzled: Boolean = boutType == "cancelled" || boutType == "fizzled"
}