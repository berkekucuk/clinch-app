package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.DeviceTokenRemoteDataSource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class DeviceTokenSupabaseAPI(
    private val client: SupabaseClient
) : DeviceTokenRemoteDataSource {

    override suspend fun upsertToken(token: String, platform: String) {
        client.postgrest.rpc(
            function = "register_device_token",
            parameters = buildJsonObject {
                put("p_fcm_token", token)
                put("p_platform", platform)
            }
        )
    }
}
