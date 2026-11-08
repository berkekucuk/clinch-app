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
    openUrl("https://apps.apple.com/tr/app/clinch-mobile/id$iosAppId")
}

actual fun openStoreReview(androidPackage: String, iosAppId: String) {
    openUrl("https://apps.apple.com/tr/app/clinch-mobile/id$iosAppId?action=write-review")
}

private fun openUrl(urlString: String) {
    val url = NSURL.URLWithString(urlString) ?: return

    UIApplication.sharedApplication.openURL(
        url,
        emptyMap<Any?, Any>(),
        null
    )
}
