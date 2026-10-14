package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.core.utils.runSuspendCatching
import com.berkekucuk.mmaapp.data.remote.dto.ManualNotificationDto
import com.berkekucuk.mmaapp.domain.enums.NotificationTarget
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions

class AdminNotificationService(
    private val client: SupabaseClient
) {
    suspend fun sendManualNotification(
        title: String,
        body: String,
        eventId: String?,
        target: NotificationTarget,
        usernames: List<String>,
    ): Result<Unit> = runSuspendCatching {
        val payload = ManualNotificationDto(
            title = title,
            body = body,
            target = target.value,
            usernames = if (target == NotificationTarget.USERS) usernames else null,
            data = if (!eventId.isNullOrBlank()) mapOf("event_id" to eventId) else null
        )
        client.functions.invoke(function = "send-notification", body = payload)
    }
}