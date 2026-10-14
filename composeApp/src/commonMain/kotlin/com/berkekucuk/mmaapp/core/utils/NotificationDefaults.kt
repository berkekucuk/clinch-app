package com.berkekucuk.mmaapp.core.utils

object NotificationDefaults {
    const val TITLE = "UFC Dövüş Gecesi 🔥"
    const val BODY = "Hemen tıkla, tahminlerini yap!"

    private val numberedEventRegex = Regex("""UFC\s+(\d+)""", RegexOption.IGNORE_CASE)

    fun titleFor(eventName: String?): String {
        val number = numberedEventRegex.find(eventName.orEmpty())?.groupValues?.get(1)
        return if (number != null) "UFC $number Dövüş Gecesi 🔥" else TITLE
    }
}
