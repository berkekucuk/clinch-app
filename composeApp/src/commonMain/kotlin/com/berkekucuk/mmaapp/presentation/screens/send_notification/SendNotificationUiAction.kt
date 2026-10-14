package com.berkekucuk.mmaapp.presentation.screens.send_notification

import com.berkekucuk.mmaapp.domain.enums.NotificationTarget

sealed interface SendNotificationUiAction {
    data class OnTitleChanged(val value: String) : SendNotificationUiAction
    data class OnBodyChanged(val value: String) : SendNotificationUiAction
    data class OnEventIdChanged(val value: String) : SendNotificationUiAction
    data class OnTargetChanged(val value: NotificationTarget) : SendNotificationUiAction
    data class OnUsernamesChanged(val value: String) : SendNotificationUiAction
    data object OnSendClicked : SendNotificationUiAction
    data object OnConfirmSend : SendNotificationUiAction
    data object OnDismissDialog : SendNotificationUiAction
    data object OnSnackbarDismissed : SendNotificationUiAction
    data object OnBackClicked : SendNotificationUiAction
}
