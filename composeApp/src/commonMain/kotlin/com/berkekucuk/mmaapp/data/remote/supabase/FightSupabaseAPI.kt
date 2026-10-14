package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.FightRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.FightDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class FightSupabaseAPI(
    private val client: SupabaseClient
) : FightRemoteDataSource {

    override suspend fun fetchFight(fightId: String): FightDto {
        return client.postgrest.rpc(
            function = "get_fight_v4",
            parameters = buildJsonObject {
                put("p_fight_id", fightId)
            }
        ).decodeList<FightDto>().first()
    }
}