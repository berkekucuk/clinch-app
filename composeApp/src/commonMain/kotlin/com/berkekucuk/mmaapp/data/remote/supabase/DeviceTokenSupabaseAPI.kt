package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.DeviceTokenRemoteDataSource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

class DeviceTokenSupabaseAPI(
    private val client: SupabaseClient
) : DeviceTokenRemoteDataSource {

    override suspend fun upsertToken(token: String, platform: String) {
        client.postgrest.rpc(
            function = "register_device_token",
            parameters = mapOf(
                "p_fcm_token" to token,
                "p_platform" to platform
            )
        )
    }
}
