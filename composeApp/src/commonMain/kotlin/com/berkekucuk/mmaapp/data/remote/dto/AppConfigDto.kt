package com.berkekucuk.mmaapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppConfigDto(
    val key: String,
    @SerialName("value_en") val valueEn: String?,
    @SerialName("value_tr") val valueTr: String?
)
