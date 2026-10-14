package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.core.utils.DateTimeProvider
import com.berkekucuk.mmaapp.data.remote.datasource.UserRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.UserDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class UserSupabaseAPI(
    private val client: SupabaseClient,
    private val dateTimeProvider: DateTimeProvider
) : UserRemoteDataSource {

    override suspend fun fetchUser(userId: String): UserDto {
        return client.postgrest.rpc(
            function = "get_profile",
            parameters = buildJsonObject {
                put("p_user_id", userId)
            }
        ).decodeList<UserDto>().first()
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

    override suspend fun reportUser(reason: String, reportedId: String?, commentId: String?) {
        client.postgrest.rpc(
            function = "report_user",
            parameters = buildJsonObject {
                put("p_reason", reason)
                reportedId?.let { put("p_reported_id", it) }
                commentId?.let { put("p_comment_id", it) }
            }
        )
    }

    override suspend fun searchUsers(query: String): List<UserDto> {
        return client.postgrest.rpc(
            function = "search_profiles",
            parameters = buildJsonObject {
                put("search_query", query)
            }
        ).decodeList<UserDto>()
    }
}
