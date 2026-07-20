package com.berkekucuk.mmaapp.data.mapper

import com.berkekucuk.mmaapp.data.local.entity.EventEntity
import com.berkekucuk.mmaapp.data.local.relation.EventWithFightsRelation
import com.berkekucuk.mmaapp.data.remote.dto.EventDto
import com.berkekucuk.mmaapp.domain.model.Event
import com.berkekucuk.mmaapp.domain.enums.EventStatus

fun EventDto.toEntity(): EventEntity {
    return EventEntity(
        eventId = this.eventId,
        name = this.name,
        status = this.status,
        datetimeUtc = this.datetimeUtc,
        venue = this.venue,
        location = this.location,
        eventYear = this.eventYear
    )
}

fun EventWithFightsRelation.toDomain(): Event {
    return Event(
        eventId = this.event.eventId,
        name = this.event.name,
        status = EventStatus.fromString(this.event.status),
        datetimeUtc = this.event.datetimeUtc,
        venue = this.event.venue,
        location = this.event.location,
        eventYear = this.event.eventYear,
        fights = this.fights
            .map { it.toDomain() }
            .sortedByDescending { it.fightOrder }
    )
}