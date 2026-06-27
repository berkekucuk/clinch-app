package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.EventRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.EventDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlin.time.Instant

class EventSupabaseAPI(
    private val client: SupabaseClient
) : EventRemoteDataSource {

    override suspend fun fetchEventById(id: String): List<EventDto> {
        return client.postgrest.rpc(
            function = "get_events_v2",
            parameters = mapOf("p_event_id" to id)
        ).decodeList<EventDto>()
    }

    override suspend fun fetchEventsByYear(year: Int): List<EventDto> {
        return client.postgrest.rpc(
            function = "get_events_v2",
            parameters = mapOf("p_year" to year)
        ).decodeList<EventDto>()
    }

    override suspend fun fetchEventsAfter(date: Instant): List<EventDto> {
        return client.postgrest.rpc(
            function = "get_events_v2",
            parameters = mapOf("p_date" to date.toString())
        ).decodeList<EventDto>()
    }
}