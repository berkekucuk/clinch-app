package com.berkekucuk.mmaapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_configs")
data class AppConfigEntity(
    @PrimaryKey val key: String,
    val valueEn: String?,
    val valueTr: String?
)
