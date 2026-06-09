package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.AppVersionRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.AppVersionDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class AppVersionSupabaseAPI(
    private val client: SupabaseClient
) : AppVersionRemoteDataSource {

    override suspend fun fetchVersionCodes(keys: List<String>): List<AppVersionDto> {
        return client.from("app_versions").select {
            filter {
                isIn("key", keys)
            }
        }.decodeList<AppVersionDto>()
    }
}
