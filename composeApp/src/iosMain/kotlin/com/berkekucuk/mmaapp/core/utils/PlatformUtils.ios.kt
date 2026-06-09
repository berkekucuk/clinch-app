package com.berkekucuk.mmaapp.core.utils

import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual val isIos: Boolean = true

actual fun getAppVersionCode(): Int {
    val versionString = NSBundle.mainBundle.infoDictionary
        ?.get("CFBundleVersion") as? String
    return versionString?.toIntOrNull() ?: 1
}

actual fun openStore(androidPackage: String, iosAppId: String) {
    val urlString = "https://apps.apple.com/tr/app/clinch-mobile/id$iosAppId"
    val url = NSURL.URLWithString(urlString) ?: return

    UIApplication.sharedApplication.openURL(
        url,
        emptyMap<Any?, Any>(),
        null
    )
}
