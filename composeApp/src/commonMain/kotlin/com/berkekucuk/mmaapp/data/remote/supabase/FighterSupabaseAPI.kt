package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.FighterRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.FighterDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

class FighterSupabaseAPI(
    private val client: SupabaseClient
) : FighterRemoteDataSource {

    override suspend fun fetchFighter(id: String): FighterDto {
        return client.postgrest.rpc(
            function = "get_fighter",
            parameters = mapOf("p_fighter_id" to id)
        ).decodeSingle<FighterDto>()
    }

    override suspend fun searchFighters(query: String): List<FighterDto> {
        return client.postgrest.rpc(
            function = "search_fighters_unaccent",
            parameters = mapOf("search_query" to query)
        ).decodeList<FighterDto>()
    }
}