package com.berkekucuk.mmaapp.domain.model

sealed class AppUpdateStatus {
    data object UpToDate : AppUpdateStatus()
    data object FlexibleUpdate : AppUpdateStatus()
    data object ForceUpdate : AppUpdateStatus()
}
