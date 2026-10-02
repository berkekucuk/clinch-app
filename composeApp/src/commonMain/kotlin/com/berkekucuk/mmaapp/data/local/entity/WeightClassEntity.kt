package com.berkekucuk.mmaapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.berkekucuk.mmaapp.data.remote.dto.RankedFighterDto

@Entity(tableName = "weight_classes")
data class WeightClassEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "sort_order") val sortOrder: Int,
    @ColumnInfo(name = "weight_limit") val weightLimit: Int? = null,
    @ColumnInfo(name = "rankings_json") val rankings: List<RankedFighterDto> = emptyList()
)
