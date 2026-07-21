package com.berkekucuk.mmaapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Instant

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey @ColumnInfo(name = "event_id") val eventId: String,
    val name: String? = null,
    val status: String? = null,
    @ColumnInfo(name = "datetime_utc") val datetimeUtc: Instant? = null,
    @ColumnInfo(name = "datetime_utc_main") val datetimeUtcMain: Instant? = null,
    val venue: String? = null,
    val location: String? = null,
    @ColumnInfo(name = "event_year") val eventYear: Int? = null
)