package com.berkekucuk.mmaapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.berkekucuk.mmaapp.data.remote.dto.ParticipantDto
import kotlin.time.Instant

@Entity(tableName = "fights")
data class FightEntity(
    @PrimaryKey @ColumnInfo(name = "fight_id") val fightId: String,
    @ColumnInfo(name = "event_id") val eventId: String,
    @ColumnInfo(name = "event_name") val eventName: String?,
    @ColumnInfo(name = "event_date") val eventDate: Instant?,
    @ColumnInfo(name = "method_type") val methodType: String?,
    @ColumnInfo(name = "method_detail") val methodDetail: String?,
    @ColumnInfo(name = "round_summary") val roundSummary: String?,
    @ColumnInfo(name = "bout_type") val boutType: String,
    @ColumnInfo(name = "weight_class_lbs") val weightClassLbs: Int?,
    @ColumnInfo(name = "weight_class_id") val weightClassId: String,
    @ColumnInfo(name = "rounds_format") val roundsFormat: String,
    @ColumnInfo(name = "fight_order") val fightOrder: Int,
    @ColumnInfo(name = "title_type") val titleType: String?,
    @ColumnInfo(name = "referee") val referee: String?,
    @ColumnInfo(name = "bonuses") val bonuses: List<String>?,
    @ColumnInfo(name = "participants") val participants: List<ParticipantDto>
)
