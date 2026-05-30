package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.AppConfigRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.AppConfigDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class AppConfigSupabaseAPI(
    private val client: SupabaseClient
) : AppConfigRemoteDataSource {

    override suspend fun fetchConfig(key: String): AppConfigDto {
        return client.from("app_configs").select {
            filter {
                eq("key", key)
            }
        }.decodeSingle<AppConfigDto>()
    }
}
