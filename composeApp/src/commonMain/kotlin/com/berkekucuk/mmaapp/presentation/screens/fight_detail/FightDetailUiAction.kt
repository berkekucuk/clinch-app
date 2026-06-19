package com.berkekucuk.mmaapp.presentation.screens.fight_detail

sealed interface FightDetailUiAction {
    data class OnFighterClicked(val fighterId: String): FightDetailUiAction
    data object OnBackClicked: FightDetailUiAction
    data object OnRefresh: FightDetailUiAction
    data class OnEventClicked(val eventId: String): FightDetailUiAction
    data class OnNotificationClicked(val isAlarm: Boolean = false): FightDetailUiAction
    data object OnErrorShown: FightDetailUiAction
    data class OnSubmitPredictionClicked(val predictedWinnerId: String, val selectedRisk: Int): FightDetailUiAction
    data object OnLeaderboardClicked: FightDetailUiAction
    data object OnOpenSettingsClicked: FightDetailUiAction
    data class OnPredictClicked(val predictedWinnerId: String): FightDetailUiAction
    data object OnDismissPredictionDialog: FightDetailUiAction
    data object OnNotificationIconClicked: FightDetailUiAction
    data object OnDismissNotificationDialog: FightDetailUiAction
}