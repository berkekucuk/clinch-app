package com.berkekucuk.mmaapp.data.mapper

import com.berkekucuk.mmaapp.data.local.entity.AppConfigEntity
import com.berkekucuk.mmaapp.data.remote.dto.AppConfigDto
import com.berkekucuk.mmaapp.domain.model.AppConfig

fun AppConfigDto.toEntity(): AppConfigEntity {
    return AppConfigEntity(
        key = key,
        valueEn = valueEn,
        valueTr = valueTr
    )
}

fun AppConfigEntity.toDomain(): AppConfig {
    return AppConfig(
        key = key,
        valueEn = valueEn ?: "",
        valueTr = valueTr ?: ""
    )
}
