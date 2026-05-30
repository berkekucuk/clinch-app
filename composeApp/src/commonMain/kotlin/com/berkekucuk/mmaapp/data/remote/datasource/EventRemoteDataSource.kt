package com.berkekucuk.mmaapp.data.remote.datasource

import com.berkekucuk.mmaapp.data.remote.dto.EventDto
import kotlin.time.Instant

interface EventRemoteDataSource {
    suspend fun fetchEventById(id: String): List<EventDto>
    suspend fun fetchEventsByYear(year: Int): List<EventDto>
    suspend fun fetchEventsAfter(date: Instant): List<EventDto>
}

