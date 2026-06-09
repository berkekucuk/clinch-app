package com.berkekucuk.mmaapp.data.remote.datasource

import com.berkekucuk.mmaapp.data.remote.dto.AppVersionDto

interface AppVersionRemoteDataSource {
    suspend fun fetchVersionCodes(keys: List<String>): List<AppVersionDto>
}