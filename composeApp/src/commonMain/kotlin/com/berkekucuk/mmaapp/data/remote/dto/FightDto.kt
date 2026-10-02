package com.berkekucuk.mmaapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class FightDto(
    @SerialName("fight_id") val fightId: String,
    @SerialName("event_id") val eventId: String,
    @SerialName("event_name") val eventName: String? = null,
    @SerialName("event_date") val eventDate: Instant? = null,
    @SerialName("method_type") val methodType: String? = null,
    @SerialName("method_detail") val methodDetail: String? = null,
    @SerialName("round_summary") val roundSummary: String? = null,
    @SerialName("bout_type") val boutType: String? = null,
    @SerialName("weight_class_lbs") val weightClassLbs: Int? = null,
    @SerialName("weight_class_id") val weightClassId: String? = null,
    @SerialName("rounds_format") val roundsFormat: String? = null,
    @SerialName("fight_order") val fightOrder: Int? = null,
    @SerialName("title_type") val titleType: String? = null,
    @SerialName("referee") val referee: String? = null,
    @SerialName("bonuses") val bonuses: List<String>? = null,
    @SerialName("participants") val participants: List<ParticipantDto>? = null,
    @SerialName("stats") val stats: List<FightStatDto>? = null
)