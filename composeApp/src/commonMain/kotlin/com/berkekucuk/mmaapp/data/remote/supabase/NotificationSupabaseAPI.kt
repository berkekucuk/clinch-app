package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.NotificationRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.FightNotificationDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class NotificationSupabaseAPI(
    private val client: SupabaseClient
) : NotificationRemoteDataSource {

    override suspend fun fetchFightNotifications(userId: String): List<FightNotificationDto> {
        return client.from("user_fight_notifications").select {
            filter {
                eq("user_id", userId)
            }
        }.decodeList<FightNotificationDto>()
    }

    override suspend fun upsertFightNotification(fightId: String, userId: String) {
        client.from("user_fight_notifications").upsert(
            FightNotificationDto(userId = userId, fightId = fightId)
        )
    }

    override suspend fun deleteFightNotification(fightId: String, userId: String) {
        client.from("user_fight_notifications").delete {
            filter {
                eq("fight_id", fightId)
                eq("user_id", userId)
            }
        }
    }
}
