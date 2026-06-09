package com.berkekucuk.mmaapp.core.utils

const val APP_ANDROID_PACKAGE = "com.berkekucuk.mmaapp"
const val APP_IOS_ID = "6769456426"

expect val isIos: Boolean

expect fun getAppVersionCode(): Int

expect fun openStore(androidPackage: String = APP_ANDROID_PACKAGE, iosAppId: String = APP_IOS_ID)