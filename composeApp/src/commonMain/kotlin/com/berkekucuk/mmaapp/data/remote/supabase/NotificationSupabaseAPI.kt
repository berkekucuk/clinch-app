package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.NotificationRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.FightNotificationDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class NotificationSupabaseAPI(
    private val client: SupabaseClient
) : NotificationRemoteDataSource {

    override suspend fun fetchFightNotifications(): List<FightNotificationDto> {
        return client.postgrest.rpc(
            function = "get_my_fight_notifications"
        ).decodeList<FightNotificationDto>()
    }

    override suspend fun upsertFightNotification(fightId: String, isAlarm: Boolean) {
        client.postgrest.rpc(
            function = "set_fight_notification",
            parameters = buildJsonObject {
                put("p_fight_id", fightId)
                put("p_is_alarm", isAlarm)
            }
        )
    }

    override suspend fun deleteFightNotification(fightId: String) {
        client.postgrest.rpc(
            function = "remove_fight_notification",
            parameters = buildJsonObject {
                put("p_fight_id", fightId)
            }
        )
    }
}
