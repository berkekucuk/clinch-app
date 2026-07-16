package com.berkekucuk.mmaapp.core.storage

interface NotificationStorage {
    fun save(isEnabled: Boolean)
    suspend fun load(): Boolean
    fun openNotificationSettings()
    fun hasRequestedPermission(): Boolean
    fun setRequestedPermission(requested: Boolean)
    fun hasFullScreenIntentPermission(): Boolean
    fun openFullScreenIntentSettings()
}
