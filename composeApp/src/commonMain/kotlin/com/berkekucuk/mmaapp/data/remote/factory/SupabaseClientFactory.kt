package com.berkekucuk.mmaapp.data.remote.factory

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.appleNativeLogin
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json

object SupabaseClientFactory {
    fun create(
        url: String,
        key: String,
        googleClientId: String
    ): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = key
        ) {
            install(Postgrest)
            install(Auth)
            install(ComposeAuth) {
                googleNativeLogin(serverClientId = googleClientId)
                appleNativeLogin()
            }
            defaultSerializer = KotlinXSerializer(
                Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                    isLenient = true
                }
            )
        }
    }
}