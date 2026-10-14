package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.FighterRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.FighterDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class FighterSupabaseAPI(
    private val client: SupabaseClient
) : FighterRemoteDataSource {

    override suspend fun fetchFighter(id: String): FighterDto {
        return client.postgrest.rpc(
            function = "get_fighter_v4",
            parameters = buildJsonObject {
                put("p_fighter_id", id)
            }
        ).decodeList<FighterDto>().first()
    }

    override suspend fun searchFighters(query: String): List<FighterDto> {
        return client.postgrest.rpc(
            function = "search_fighters_unaccent",
            parameters = buildJsonObject {
                put("search_query", query)
            }
        ).decodeList<FighterDto>()
    }
}