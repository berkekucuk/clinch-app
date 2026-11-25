package com.berkekucuk.mmaapp.core.storage

import android.content.Context
import androidx.core.content.edit
import kotlin.time.Instant

class AndroidEventCommentSeenStorage(context: Context) : EventCommentSeenStorage {
    private val prefs = context.getSharedPreferences("event_comment_seen_prefs", Context.MODE_PRIVATE)

    override fun getLastSeenAt(eventId: String): Instant? {
        return prefs.getString(eventId, null)?.let { runCatching { Instant.parse(it) }.getOrNull() }
    }

    override fun saveLastSeenAt(eventId: String, seenAt: Instant) {
        prefs.edit { putString(eventId, seenAt.toString()) }
    }
}
