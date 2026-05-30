package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.DeviceTokenRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.DeviceTokenDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class DeviceTokenSupabaseAPI(
    private val client: SupabaseClient
) : DeviceTokenRemoteDataSource {

    override suspend fun upsertToken(token: String, userId: String, platform: String) {
        client.from("user_device_tokens").upsert(
            DeviceTokenDto(fcmToken = token, userId = userId, platform = platform)
        )
    }

    override suspend fun deleteToken(token: String) {
        client.from("user_device_tokens").delete {
            filter { eq("fcm_token", token) }
        }
    }
}
