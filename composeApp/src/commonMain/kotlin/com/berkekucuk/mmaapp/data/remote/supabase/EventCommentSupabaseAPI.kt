package com.berkekucuk.mmaapp.data.remote.supabase

import com.berkekucuk.mmaapp.data.remote.datasource.EventCommentRealtimeEvent
import com.berkekucuk.mmaapp.data.remote.datasource.EventCommentRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.dto.EventCommentDeletedDto
import com.berkekucuk.mmaapp.data.remote.dto.EventCommentDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.broadcastFlow
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.time.Instant

class EventCommentSupabaseAPI(
    private val client: SupabaseClient
) : EventCommentRemoteDataSource {

    override suspend fun fetchComments(
        eventId: String,
        limit: Int,
        beforeCreatedAt: Instant?,
        beforeId: String?,
        afterCreatedAt: Instant?,
        afterId: String?,
    ): List<EventCommentDto> {
        return client.postgrest.rpc(
            function = "get_event_comments",
            parameters = buildJsonObject {
                put("p_event_id", eventId)
                put("p_limit", limit)
                if (beforeCreatedAt != null && beforeId != null) {
                    put("p_before_created_at", beforeCreatedAt.toString())
                    put("p_before_id", beforeId)
                }
                if (afterCreatedAt != null && afterId != null) {
                    put("p_after_created_at", afterCreatedAt.toString())
                    put("p_after_id", afterId)
                }
            }
        ).decodeList<EventCommentDto>()
    }

    override suspend fun addComment(eventId: String, content: String): EventCommentDto {
        return client.postgrest.rpc(
            function = "add_event_comment",
            parameters = buildJsonObject {
                put("p_event_id", eventId)
                put("p_content", content)
            }
        ).decodeList<EventCommentDto>().first()
    }

    override suspend fun deleteComment(commentId: String) {
        client.postgrest.rpc(
            function = "delete_event_comment",
            parameters = buildJsonObject {
                put("p_comment_id", commentId)
            }
        )
    }

    override suspend fun banUser(userId: String, eventId: String?) {
        client.postgrest.rpc(
            function = "ban_user_from_comments",
            parameters = buildJsonObject {
                put("p_user_id", userId)
                eventId?.let { put("p_event_id", it) }
            }
        )
    }

    override suspend fun unbanUser(userId: String, eventId: String?) {
        client.postgrest.rpc(
            function = "unban_user_from_comments",
            parameters = buildJsonObject {
                put("p_user_id", userId)
                eventId?.let { put("p_event_id", it) }
            }
        )
    }

    override fun observeComments(eventId: String): Flow<EventCommentRealtimeEvent> = channelFlow {
        val channel = client.channel("event_comments:$eventId") {
            isPrivate = true
        }

        launch {
            channel.broadcastFlow<EventCommentDto>(event = "comment_added")
                .collect { send(EventCommentRealtimeEvent.Added(it)) }
        }
        launch {
            channel.broadcastFlow<EventCommentDeletedDto>(event = "comment_deleted")
                .collect { send(EventCommentRealtimeEvent.Deleted(it.id)) }
        }
        launch {
            channel.status
                .filter { it == RealtimeChannel.Status.SUBSCRIBED }
                .collect { send(EventCommentRealtimeEvent.Subscribed) }
        }

        try {
            channel.subscribe()
            awaitCancellation()
        } finally {
            withContext(NonCancellable) {
                client.realtime.removeChannel(channel)
            }
        }
    }
}
