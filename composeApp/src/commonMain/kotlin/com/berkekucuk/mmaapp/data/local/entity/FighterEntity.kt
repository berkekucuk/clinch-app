package com.berkekucuk.mmaapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.berkekucuk.mmaapp.data.remote.dto.MeasurementDto
import com.berkekucuk.mmaapp.data.remote.dto.RecordDto

@Entity(tableName = "fighters")
data class FighterEntity(
    @PrimaryKey @ColumnInfo(name = "fighter_id") val fighterId: String,
    val name: String? = null,
    val nickname: String? = null,
    @ColumnInfo(name = "image_url") val imageUrl: String? = null,
    @ColumnInfo(name = "record_json") val record: RecordDto? = null,
    @ColumnInfo(name = "height_json") val height: MeasurementDto? = null,
    @ColumnInfo(name = "reach_json") val reach: MeasurementDto? = null,
    @ColumnInfo(name = "weight_class_id") val weightClassId: String? = null,
    @ColumnInfo(name = "date_of_birth") val dateOfBirth: String? = null,
    val born: String? = null,
    @ColumnInfo(name = "fighting_out_of") val fightingOutOf: String? = null,
    @ColumnInfo(name = "country_code") val countryCode: String? = null,
    @ColumnInfo(name = "win_rate") val winRate: Float? = null,
    @ColumnInfo(name = "ko_tko_rate") val koTkoRate: Float? = null,
    @ColumnInfo(name = "submission_rate") val submissionRate: Float? = null
)
