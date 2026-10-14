package com.berkekucuk.mmaapp.presentation.screens.event_detail

sealed interface EventDetailNavigationEvent {
    data class ToFightDetail(val fightId: String) : EventDetailNavigationEvent
    data object Back : EventDetailNavigationEvent
    data class ToComments(val eventId: String, val eventName: String?) : EventDetailNavigationEvent
}
