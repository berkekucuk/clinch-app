package com.berkekucuk.mmaapp.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class AppConfig(
    val key: String,
    val valueEn: String,
    val valueTr: String
)
