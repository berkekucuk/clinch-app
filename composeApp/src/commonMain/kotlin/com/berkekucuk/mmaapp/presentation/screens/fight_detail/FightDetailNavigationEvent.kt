package com.berkekucuk.mmaapp.presentation.screens.fight_detail

sealed interface FightDetailNavigationEvent {
    data class ToFighterDetail(val fighterId: String) : FightDetailNavigationEvent
    data object Back : FightDetailNavigationEvent
    data class ToEventDetail(val eventId: String) : FightDetailNavigationEvent
    data object RequestNotificationPermission : FightDetailNavigationEvent
    data object ToLeaderboard : FightDetailNavigationEvent
}