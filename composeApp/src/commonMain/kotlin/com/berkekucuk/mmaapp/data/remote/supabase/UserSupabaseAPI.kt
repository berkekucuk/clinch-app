package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.core.utils.DateTimeProvider
import com.berkekucuk.mmaapp.data.remote.datasource.UserRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.UserDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.postgrest.rpc

class UserSupabaseAPI(
    private val client: SupabaseClient,
    private val dateTimeProvider: DateTimeProvider
) : UserRemoteDataSource {

    override suspend fun fetchUser(userId: String): UserDto {
        return client.from("profile_view_v6").select {
            filter {
                eq("id", userId)
            }
        }.decodeSingle<UserDto>()
    }

    override suspend fun updateUser(userId: String, fullName: String, username: String, avatarUrl: String){
        client.from("profiles").update({
            set("username", username)
            set("full_name", fullName)
            set("avatar_url", avatarUrl)
        }) {
            filter {
                eq("id", userId)
            }
        }
    }

    override suspend fun uploadAvatar(userId: String, imageBytes: ByteArray): String {
        val bucket = client.storage.from("avatars")
        val path = "$userId/avatar_${dateTimeProvider.now.toEpochMilliseconds()}.jpg"
        bucket.upload(path, imageBytes) {
            upsert = true
        }
        return bucket.publicUrl(path)
    }

    override suspend fun deleteUser(userId: String) {
        client.postgrest.rpc("delete_my_account")
    }

    override suspend fun reportUser(reporterId: String, reportedId: String, reason: String) {
        client.from("user_reports").insert(
            mapOf(
                "reporter_id" to reporterId,
                "reported_id" to reportedId,
                "reason" to reason
            )
        )
    }

    override suspend fun searchUsers(query: String): List<UserDto> {
        return client.postgrest.rpc(
            function = "search_profiles",
            parameters = mapOf("search_query" to query)
        ).decodeList<UserDto>()
    }
}
