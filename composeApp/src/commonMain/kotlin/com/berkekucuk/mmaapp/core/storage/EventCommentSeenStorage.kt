package com.berkekucuk.mmaapp.core.storage

import kotlin.time.Instant

interface EventCommentSeenStorage {
    fun getLastSeenAt(eventId: String): Instant?
    fun saveLastSeenAt(eventId: String, seenAt: Instant)
}
