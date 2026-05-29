package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.EventRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.EventDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlin.time.Instant

class EventSupabaseAPI(
    private val client: SupabaseClient
) : EventRemoteDataSource {

    override suspend fun fetchEventById(id: String): List<EventDto> {
        return client.from("event_view").select {
            filter {
                eq("event_id", id)
            }
        }.decodeList<EventDto>()
    }

    override suspend fun fetchEventsByYear(year: Int): List<EventDto> {
        return client.from("event_view").select {
            filter {
                eq("event_year", year)
            }
            order(column = "datetime_utc", order = Order.DESCENDING)
        }.decodeList<EventDto>()
    }

    override suspend fun fetchEventsAfter(date: Instant): List<EventDto> {
        return client.from("event_view").select {
            filter {
                gte("datetime_utc", date)
            }
            order(column = "datetime_utc", order = Order.ASCENDING)
        }.decodeList<EventDto>()
    }
}