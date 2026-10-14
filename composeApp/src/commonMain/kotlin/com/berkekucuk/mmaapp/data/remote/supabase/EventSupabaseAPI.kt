package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.EventRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.EventDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlin.time.Instant
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class EventSupabaseAPI(
    private val client: SupabaseClient
) : EventRemoteDataSource {

    override suspend fun fetchEventById(id: String): List<EventDto> {
        return client.postgrest.rpc(
            function = "get_events_v4",
            parameters = buildJsonObject {
                put("p_event_id", id)
            }
        ).decodeList<EventDto>()
    }

    override suspend fun fetchEventsByYear(year: Int): List<EventDto> {
        return client.postgrest.rpc(
            function = "get_events_v4",
            parameters = buildJsonObject {
                put("p_year", year)
            }
        ).decodeList<EventDto>()
    }

    override suspend fun fetchEventsAfter(date: Instant): List<EventDto> {
        return client.postgrest.rpc(
            function = "get_events_v4",
            parameters = buildJsonObject {
                put("p_date", date.toString())
            }
        ).decodeList<EventDto>()
    }
}