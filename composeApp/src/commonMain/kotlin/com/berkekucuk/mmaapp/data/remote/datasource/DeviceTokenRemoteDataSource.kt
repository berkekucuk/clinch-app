package com.berkekucuk.mmaapp.data.remote.datasource

interface DeviceTokenRemoteDataSource {
    suspend fun upsertToken(token: String, userId: String, platform: String)
    suspend fun deleteToken(token: String)
}
