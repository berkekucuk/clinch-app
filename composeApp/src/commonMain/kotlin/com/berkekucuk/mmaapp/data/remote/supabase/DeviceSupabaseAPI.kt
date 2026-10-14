package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.DeviceRemoteDataSource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class DeviceSupabaseAPI(
    private val client: SupabaseClient
) : DeviceRemoteDataSource {

    override suspend fun registerDevice(token: String, platform: String, appVersionCode: Int) {
        client.postgrest.rpc(
            function = "register_device",
            parameters = buildJsonObject {
                put("p_fcm_token", token)
                put("p_platform", platform)
                put("p_app_version_code", appVersionCode)
            }
        )
    }
}
