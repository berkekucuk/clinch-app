package com.berkekucuk.mmaapp.core.storage

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSURL
import platform.Foundation.NSUserDefaults.Companion.standardUserDefaults
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenNotificationSettingsURLString
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import kotlin.coroutines.resume

class IosNotificationStorage : NotificationStorage {
    override fun save(isEnabled: Boolean) {
        // Controlled by system
    }

    override suspend fun load(): Boolean = suspendCancellableCoroutine { continuation ->
        UNUserNotificationCenter.currentNotificationCenter()
            .getNotificationSettingsWithCompletionHandler { settings ->
                val isEnabled = settings?.authorizationStatus == UNAuthorizationStatusAuthorized
                continuation.resume(isEnabled)
            }
    }

    override fun openNotificationSettings() {
        val url = NSURL.URLWithString(UIApplicationOpenNotificationSettingsURLString)
        if (url != null) {
            UIApplication.sharedApplication.openURL(
                url = url,
                options = emptyMap<Any?, Any>(),
                completionHandler = null
            )
        }
    }

    override fun hasRequestedPermission(): Boolean {
        return standardUserDefaults.boolForKey("has_requested_permission")
    }

    override fun setRequestedPermission(requested: Boolean) {
        standardUserDefaults.setBool(requested, "has_requested_permission")
    }

    override fun hasFullScreenIntentPermission(): Boolean {
        return true
    }

    override fun openFullScreenIntentSettings() {
        // No-op for iOS
    }
}
