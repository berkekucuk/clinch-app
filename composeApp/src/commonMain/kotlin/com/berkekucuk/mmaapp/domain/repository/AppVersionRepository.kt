package com.berkekucuk.mmaapp.domain.repository

import com.berkekucuk.mmaapp.domain.model.AppUpdateStatus

interface AppVersionRepository {
    suspend fun checkForUpdate(): Result<AppUpdateStatus>
}
