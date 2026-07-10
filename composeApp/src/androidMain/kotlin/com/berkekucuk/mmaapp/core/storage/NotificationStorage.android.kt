package com.berkekucuk.mmaapp.core.storage

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import android.app.NotificationManager

class AndroidNotificationStorage(private val context: Context) : NotificationStorage {
    override fun save(isEnabled: Boolean) {
        // Controlled by system
    }

    override suspend fun load(): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    override fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    private val prefs = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)

    override fun hasRequestedPermission(): Boolean {
        return prefs.getBoolean("has_requested_permission", false)
    }

    override fun setRequestedPermission(requested: Boolean) {
        prefs.edit { putBoolean("has_requested_permission", requested) }
    }

    override fun hasFullScreenIntentPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            return notificationManager.canUseFullScreenIntent()
        }
        return true
    }

    override fun openFullScreenIntentSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
                data = "package:${context.packageName}".toUri()
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}
