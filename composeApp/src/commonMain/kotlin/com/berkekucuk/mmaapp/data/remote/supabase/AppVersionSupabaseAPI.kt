package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.AppVersionRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.AppVersionDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonArray

class AppVersionSupabaseAPI(
    private val client: SupabaseClient
) : AppVersionRemoteDataSource {

    override suspend fun fetchVersionCodes(keys: List<String>): List<AppVersionDto> {
        return client.postgrest.rpc(
            function = "get_app_versions",
            parameters = buildJsonObject {
                putJsonArray("p_keys") {
                    keys.forEach { add(it) }
                }
            }
        ).decodeList<AppVersionDto>()
    }
}
