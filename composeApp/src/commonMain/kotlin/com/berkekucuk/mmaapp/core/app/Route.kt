package com.berkekucuk.mmaapp.core.app

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable data object MainGraph : Route
    @Serializable data object Home : Route
    @Serializable data object Rankings : Route
    @Serializable data object Menu : Route
    @Serializable data class Profile(val userId: String) : Route
    @Serializable data object ProfileEdit : Route
    @Serializable data class EventDetail(val eventId: String, val fromFightDetail: Boolean = false) : Route
    @Serializable data class FightDetail(val fightId: String, val fighterId: String? = null, val fromEventDetail: Boolean = false) : Route
    @Serializable data class FighterDetail(val fighterId: String) : Route
    @Serializable data class RankingDetail(val weightClassId: String) : Route
    @Serializable
    data object UserSearch : Route

    @Serializable data class InteractionList(val userId: String, val interactionType: String) : Route
    @Serializable data class FighterSearch(val interactionType: String? = null) : Route
    @Serializable data object Settings : Route
    @Serializable data object Leaderboard : Route
    @Serializable data object BlockedUsers : Route
}
