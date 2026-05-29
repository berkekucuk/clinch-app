package com.berkekucuk.mmaapp.data.remote.datasource

import com.berkekucuk.mmaapp.data.remote.dto.WeightClassDto

interface WeightClassRemoteDataSource {
    suspend fun fetchWeightClasses(): List<WeightClassDto>
}