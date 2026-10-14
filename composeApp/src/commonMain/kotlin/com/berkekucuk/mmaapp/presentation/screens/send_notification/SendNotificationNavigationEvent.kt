package com.berkekucuk.mmaapp.presentation.screens.send_notification

sealed interface SendNotificationNavigationEvent {
    data object Back : SendNotificationNavigationEvent
}
