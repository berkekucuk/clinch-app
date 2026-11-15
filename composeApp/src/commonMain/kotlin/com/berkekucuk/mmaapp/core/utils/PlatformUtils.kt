package com.berkekucuk.mmaapp.core.utils

const val APP_ANDROID_PACKAGE = "com.berkekucuk.mmaapp"
const val APP_IOS_ID = "6769456426"
const val SUPPORT_EMAIL = "support@clinchmobile.app"
const val X_USERNAME = "clinchmobileapp"

expect val isIos: Boolean

expect fun getAppVersionCode(): Int

expect fun openStore(androidPackage: String = APP_ANDROID_PACKAGE, iosAppId: String = APP_IOS_ID)

expect fun openStoreReview(androidPackage: String = APP_ANDROID_PACKAGE, iosAppId: String = APP_IOS_ID)

expect fun clearImageCache()
