package com.berkekucuk.mmaapp.presentation.screens.send_notification

import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.core.utils.NotificationDefaults
import com.berkekucuk.mmaapp.domain.enums.NotificationTarget

data class SendNotificationUiState(
    val title: String = NotificationDefaults.TITLE,
    val body: String = NotificationDefaults.BODY,
    val eventId: String = "",
    val target: NotificationTarget = NotificationTarget.USERS,
    val userIds: String = "",
    val showConfirmDialog: Boolean = false,
    val isSending: Boolean = false,
    val error: AppError? = null,
) {
    val parsedUserIds: List<String>
        get() = userIds.split(',', '\n', ' ').map { it.trim() }.filter { it.isNotEmpty() }

    val canSend: Boolean
        get() = title.isNotBlank() &&
            body.isNotBlank() &&
            (target != NotificationTarget.USERS || parsedUserIds.isNotEmpty()) &&
            !isSending
}
