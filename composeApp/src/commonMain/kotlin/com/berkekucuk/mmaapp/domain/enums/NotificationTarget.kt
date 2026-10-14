package com.berkekucuk.mmaapp.domain.enums

enum class NotificationTarget(val value: String, val label: String) {
    ALL("all", "All"),
    USERS("users", "Users"),
    ANDROID("android", "Android"),
    IOS("ios", "iOS"),
}
