package com.berkekucuk.mmaapp.data.remote.datasource

interface DeviceRemoteDataSource {
    suspend fun registerDevice(token: String, platform: String, appVersionCode: Int)
}
