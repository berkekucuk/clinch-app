package com.berkekucuk.mmaapp.domain.enums

enum class ReportReason {
    INAPPROPRIATE_PROFILE_PICTURE,
    INAPPROPRIATE_USERNAME,
    SPAM_OR_BOT,
    ABUSIVE_OR_HARASSING_BEHAVIOR,
    HATE_SPEECH,
    INAPPROPRIATE_CONTENT,
    SPOILER,
    OTHER;

    val dbValue: String
        get() = when (this) {
            INAPPROPRIATE_PROFILE_PICTURE -> "Inappropriate profile picture"
            INAPPROPRIATE_USERNAME -> "Inappropriate username"
            SPAM_OR_BOT -> "Spam or bot"
            ABUSIVE_OR_HARASSING_BEHAVIOR -> "Abusive or harassing behavior"
            HATE_SPEECH -> "Hate speech"
            INAPPROPRIATE_CONTENT -> "Inappropriate content"
            SPOILER -> "Spoiler"
            OTHER -> "Other"
        }

    companion object {
        val profileReasons = listOf(
            INAPPROPRIATE_PROFILE_PICTURE,
            INAPPROPRIATE_USERNAME,
            SPAM_OR_BOT,
            ABUSIVE_OR_HARASSING_BEHAVIOR,
            OTHER,
        )

        val commentReasons = listOf(
            SPAM_OR_BOT,
            ABUSIVE_OR_HARASSING_BEHAVIOR,
            HATE_SPEECH,
            INAPPROPRIATE_CONTENT,
            SPOILER,
            OTHER,
        )
    }
}