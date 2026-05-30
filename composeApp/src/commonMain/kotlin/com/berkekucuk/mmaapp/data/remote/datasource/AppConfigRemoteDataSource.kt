package com.berkekucuk.mmaapp.data.remote.datasource

import com.berkekucuk.mmaapp.data.remote.dto.AppConfigDto

interface AppConfigRemoteDataSource {
    suspend fun fetchConfig(key: String): AppConfigDto
}
