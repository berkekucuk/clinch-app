package com.berkekucuk.mmaapp.data.repository

import com.berkekucuk.mmaapp.core.storage.AppVersionStorage
import com.berkekucuk.mmaapp.core.utils.getAppVersionCode
import com.berkekucuk.mmaapp.core.utils.isIos
import com.berkekucuk.mmaapp.data.remote.datasource.AppVersionRemoteDataSource
import com.berkekucuk.mmaapp.domain.model.AppUpdateStatus
import com.berkekucuk.mmaapp.domain.repository.AppVersionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

private const val ANDROID_LATEST_KEY = "android_latest_version_code"
private const val ANDROID_MIN_REQUIRED_KEY = "android_min_required_version_code"
private const val IOS_LATEST_KEY = "ios_latest_version_code"
private const val IOS_MIN_REQUIRED_KEY = "ios_min_required_version_code"

class AppVersionRepositoryImpl(
    private val remoteDataSource: AppVersionRemoteDataSource,
    private val appVersionStorage: AppVersionStorage
) : AppVersionRepository {

    override suspend fun checkForUpdate(): Result<AppUpdateStatus> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val latestKey = if (isIos) IOS_LATEST_KEY else ANDROID_LATEST_KEY
                val minRequiredKey = if (isIos) IOS_MIN_REQUIRED_KEY else ANDROID_MIN_REQUIRED_KEY

                val versionList = remoteDataSource.fetchVersionCodes(listOf(latestKey, minRequiredKey))
                val latestVersionCode = versionList.find { it.key == latestKey }?.value ?: 1
                val minRequiredVersionCode = versionList.find { it.key == minRequiredKey }?.value ?: 1
                
                appVersionStorage.saveMinRequiredVersion(minRequiredVersionCode)
                
                val currentVersionCode = getAppVersionCode()

                when {
                    currentVersionCode < minRequiredVersionCode -> AppUpdateStatus.ForceUpdate
                    currentVersionCode < latestVersionCode -> AppUpdateStatus.FlexibleUpdate
                    else -> AppUpdateStatus.UpToDate
                }
            }.recoverCatching { error ->
                if (error is CancellationException) throw error
                
                val cachedMinRequired = appVersionStorage.getMinRequiredVersion()
                val currentVersionCode = getAppVersionCode()
                
                if (currentVersionCode < cachedMinRequired) {
                    AppUpdateStatus.ForceUpdate
                } else {
                    AppUpdateStatus.UpToDate
                }
            }
        }
    }
}
