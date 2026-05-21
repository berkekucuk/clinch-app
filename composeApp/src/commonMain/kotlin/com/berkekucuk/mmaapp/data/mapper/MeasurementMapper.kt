package com.berkekucuk.mmaapp.data.mapper

import com.berkekucuk.mmaapp.data.remote.dto.MeasurementDto
import com.berkekucuk.mmaapp.domain.model.Measurement

fun MeasurementDto.toDomain(): Measurement {
    return Measurement(
        metric = metric,
        imperial = imperial
    )
}

fun Measurement.toDto(): MeasurementDto {
    return MeasurementDto(
        metric = metric,
        imperial = imperial
    )
}