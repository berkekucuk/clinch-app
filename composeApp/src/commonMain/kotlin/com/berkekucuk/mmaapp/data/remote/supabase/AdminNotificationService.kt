package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.dto.ManualNotificationDto
import com.berkekucuk.mmaapp.domain.enums.NotificationTarget
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class AdminNotificationService(
    private val client: SupabaseClient
) {
    suspend fun sendManualNotification(
        title: String,
        body: String,
        eventId: String?,
        target: NotificationTarget,
        userIds: List<String>,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val payload = ManualNotificationDto(
                title = title,
                body = body,
                target = target.value,
                userIds = if (target == NotificationTarget.USERS) userIds else null,
                data = if (!eventId.isNullOrBlank()) mapOf("event_id" to eventId) else null
            )
            client.functions.invoke(function = "send-notification", body = payload)
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}