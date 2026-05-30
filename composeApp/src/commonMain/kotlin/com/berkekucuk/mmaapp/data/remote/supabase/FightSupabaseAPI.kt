package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.FightRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.FightDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

class FightSupabaseAPI(
    private val client: SupabaseClient
) : FightRemoteDataSource {

    override suspend fun fetchFight(fightId: String): FightDto {
        return client.postgrest.rpc(
            function = "get_fight",
            parameters = mapOf("p_fight_id" to fightId)
        ).decodeSingle<FightDto>()
    }
}