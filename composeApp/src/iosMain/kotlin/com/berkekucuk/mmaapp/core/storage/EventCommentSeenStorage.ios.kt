package com.berkekucuk.mmaapp.core.storage

import platform.Foundation.NSUserDefaults
import kotlin.time.Instant

class IosEventCommentSeenStorage : EventCommentSeenStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    private fun key(eventId: String) = "event_comment_seen_$eventId"

    override fun getLastSeenAt(eventId: String): Instant? {
        return defaults.stringForKey(key(eventId))?.let { runCatching { Instant.parse(it) }.getOrNull() }
    }

    override fun saveLastSeenAt(eventId: String, seenAt: Instant) {
        defaults.setObject(seenAt.toString(), forKey = key(eventId))
    }
}
