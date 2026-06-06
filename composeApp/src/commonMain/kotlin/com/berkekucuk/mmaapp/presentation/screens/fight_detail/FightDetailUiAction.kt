package com.berkekucuk.mmaapp.presentation.screens.fight_detail

sealed interface FightDetailUiAction {
    data class OnFighterClicked(val fighterId: String): FightDetailUiAction
    data object OnBackClicked: FightDetailUiAction
    data object OnRefresh: FightDetailUiAction
    data class OnEventClicked(val eventId: String): FightDetailUiAction
    data object OnNotificationClicked: FightDetailUiAction
    data object OnErrorShown: FightDetailUiAction
    data object OnResume: FightDetailUiAction
    data class OnSubmitPredictionClicked(val predictedWinnerId: String): FightDetailUiAction
    data object OnLeaderboardClicked: FightDetailUiAction
    data object OnOpenSettingsClicked: FightDetailUiAction
}