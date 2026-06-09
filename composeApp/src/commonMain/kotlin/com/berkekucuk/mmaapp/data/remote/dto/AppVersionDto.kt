package com.berkekucuk.mmaapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AppVersionDto(
    val key: String,
    val value: Int
)
