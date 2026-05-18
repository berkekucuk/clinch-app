package com.berkekucuk.mmaapp.data.remote.api

import com.berkekucuk.mmaapp.data.remote.dto.FighterDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

class FighterSupabaseAPI(
    private val client: SupabaseClient
) : FighterRemoteDataSource {

    override suspend fun fetchFighter(id: String): FighterDto {
        return client.from("fighter_view").select {
            filter {
                eq("fighter_id", id)
            }
        }.decodeSingle<FighterDto>()
    }

    override suspend fun searchFighters(query: String): List<FighterDto> {
        return client.postgrest.rpc(
            function = "search_fighters_unaccent",
            parameters = mapOf("search_query" to query)
        ).decodeList<FighterDto>()
    }
}