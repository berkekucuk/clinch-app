package com.berkekucuk.mmaapp.core.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import androidx.core.net.toUri

actual val isIos: Boolean = false

private object AndroidPlatformContext : KoinComponent {
    val context: Context by inject()
}

actual fun getAppVersionCode(): Int {
    val context = AndroidPlatformContext.context
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode.toInt()
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode
        }
    } catch (_: Exception) {
        1
    }
}

actual fun openStore(androidPackage: String, iosAppId: String) {
    openPlayStorePage(androidPackage)
}

actual fun openStoreReview(androidPackage: String, iosAppId: String) {
    openPlayStorePage(androidPackage)
}

private fun openPlayStorePage(androidPackage: String) {
    val context = AndroidPlatformContext.context
    val marketUri = "market://details?id=$androidPackage".toUri()
    val webUri = "https://play.google.com/store/apps/details?id=$androidPackage".toUri()

    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, marketUri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (_: ActivityNotFoundException) {
        context.startActivity(Intent(Intent.ACTION_VIEW, webUri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}