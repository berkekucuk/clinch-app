package com.berkekucuk.mmaapp.presentation.screens.fight_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.berkekucuk.mmaapp.core.app.Route
import com.berkekucuk.mmaapp.core.utils.AppErrorMapper
import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.domain.repository.AuthRepository
import com.berkekucuk.mmaapp.domain.repository.NotificationRepository
import com.berkekucuk.mmaapp.domain.repository.PredictionRepository
import com.berkekucuk.mmaapp.core.storage.NotificationStorage
import com.berkekucuk.mmaapp.domain.model.Fight
import com.berkekucuk.mmaapp.domain.repository.FightRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FightDetailViewModel(
    private val fightRepository: FightRepository,
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository,
    private val predictionRepository: PredictionRepository,
    private val notificationStorage: NotificationStorage,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<Route.FightDetail>()
    private val fightId: String = route.fightId
    private val fighterId: String? = route.fighterId
    val fromEventDetail: Boolean = route.fromEventDetail
    private val _state = MutableStateFlow(FightDetailUiState())
    val state = _state.asStateFlow()
    private val _navigation = MutableSharedFlow<FightDetailNavigationEvent>()
    val navigation = _navigation.asSharedFlow()
    private var refreshJob: Job? = null

    init {
        observeFight()
        observeFightNotificationStatus()
        observePredictionStatus()
    }

    private fun observeFight() {
        viewModelScope.launch {
            fightRepository.getFight(fightId)
                .collect { fight ->
                    _state.update {
                        it.copy(
                            fight = fight,
                            showPredictionBoard = !isFightCompleted(fight)
                        )
                    }
                }
        }
    }

    private fun observeFightNotificationStatus() {
        viewModelScope.launch {
            val userId = authRepository.getAuthenticatedUserId()
            if (userId != null) {
                notificationRepository.getFightNotificationStatus(fightId, userId)
                    .collect { isEnabled ->
                        _state.update { it.copy(isNotificationEnabled = isEnabled) }
                    }
            }
        }
    }

    private fun observePredictionStatus() {
        viewModelScope.launch {
            val userId = authRepository.getAuthenticatedUserId()
            if (userId != null) {
                predictionRepository.getPredictedWinnerId(fightId, userId)
                    .collect { predictedId ->
                        _state.update { it.copy(predictedWinnerId = predictedId) }
                    }
            }
        }
    }

    fun onAction(action: FightDetailUiAction) {
        when (action) {
            is FightDetailUiAction.OnFighterClicked -> {
                if (action.fighterId == fighterId) {
                    navigateTo(FightDetailNavigationEvent.Back)
                } else {
                    navigateTo(FightDetailNavigationEvent.ToFighterDetail(action.fighterId))
                }
            }
            is FightDetailUiAction.OnBackClicked -> navigateTo(FightDetailNavigationEvent.Back)
            is FightDetailUiAction.OnRefresh -> onRefresh()
            is FightDetailUiAction.OnEventClicked -> navigateTo(FightDetailNavigationEvent.ToEventDetail(action.eventId))
            is FightDetailUiAction.OnErrorShown -> _state.update { it.copy(error = null) }
            is FightDetailUiAction.OnLeaderboardClicked -> navigateTo(FightDetailNavigationEvent.ToLeaderboard)

            // Notifications
            is FightDetailUiAction.OnNotificationIconClicked -> {
                _state.update { it.copy(showNotificationDialog = true) }
            }
            is FightDetailUiAction.OnSubmitNotificationClicked -> submitNotification(action.isAlarm)
            is FightDetailUiAction.OnDismissNotificationDialog -> {
                _state.update { it.copy(showNotificationDialog = false) }
            }
            is FightDetailUiAction.OnOpenNotificationSettingsClicked -> {
                _state.update { it.copy(showNotificationSettingsDialog = false) }
                notificationStorage.openNotificationSettings()
            }
            is FightDetailUiAction.OnDismissNotificationSettingsDialog -> {
                _state.update { it.copy(showNotificationSettingsDialog = false) }
            }
            is FightDetailUiAction.OnOpenFullScreenIntentSettingsClicked -> {
                _state.update { it.copy(showFullScreenIntentSettingsDialog = false) }
                notificationStorage.openFullScreenIntentSettings()
            }
            is FightDetailUiAction.OnDismissFullScreenIntentSettingsDialog -> {
                _state.update { it.copy(showFullScreenIntentSettingsDialog = false) }
            }

            // Predictions
            is FightDetailUiAction.OnPredictClicked -> {
                _state.update { it.copy(showPredictionDialog = true, pendingPredictionFighterId = action.predictedWinnerId) }
            }
            is FightDetailUiAction.OnSubmitPredictionClicked -> submitPrediction(action.predictedWinnerId, action.selectedRisk)
            is FightDetailUiAction.OnDismissPredictionDialog -> {
                _state.update { it.copy(showPredictionDialog = false, pendingPredictionFighterId = null) }
            }
        }
    }

    private fun submitNotification(isAlarm: Boolean) {
        viewModelScope.launch {
            val userId = authRepository.getAuthenticatedUserId()
            if (userId == null) {
                _state.update { it.copy(error = AppError.UNAUTHENTICATED, showNotificationDialog = false) }
                return@launch
            }

            val fight = _state.value.fight ?: return@launch
            val isNotificationEnabled = _state.value.isNotificationEnabled
            if (!canToggleNotification(fight, isNotificationEnabled)) return@launch

            if (isNotificationEnabled) {
                removeNotification(fight.fightId, userId)
            } else {
                submitNotificationWithPermissionCheck(fight.fightId, userId, isAlarm)
            }
        }
    }

    private fun isFightCompleted(fight: Fight): Boolean {
        return fight.methodType.isNotBlank() || fight.methodDetail.isNotBlank()
    }

    private fun canToggleNotification(fight: Fight, isNotificationEnabled: Boolean): Boolean {
        if (isFightCompleted(fight) && !isNotificationEnabled) {
            _state.update { it.copy(error = AppError.FIGHT_OVER, showNotificationDialog = false) }
            return false
        }
        return true
    }

    private suspend fun removeNotification(fightId: String, userId: String) {
        _state.update { it.copy(isSubmittingNotification = true, error = null) }
        notificationRepository.removeFightNotification(fightId, userId)
            .onSuccess {
                _state.update { it.copy(showNotificationDialog = false) }
            }
            .onFailure { e ->
                _state.update { it.copy(showNotificationDialog = false, error = AppErrorMapper.map(e)) }
            }
        _state.update { it.copy(isSubmittingNotification = false) }
    }

    private suspend fun submitNotificationWithPermissionCheck(fightId: String, userId: String, isAlarm: Boolean) {
        if (!notificationStorage.load()) {
            handleMissingPermission()
            return
        }

        if (isAlarm && !notificationStorage.hasFullScreenIntentPermission()) {
            _state.update { it.copy(showNotificationDialog = false, showFullScreenIntentSettingsDialog = true) }
            return
        }

        _state.update { it.copy(isSubmittingNotification = true, error = null) }
        notificationRepository.addFightNotification(fightId, userId, isAlarm)
            .onSuccess {
                _state.update { it.copy(showNotificationDialog = false) }
            }
            .onFailure { e ->
                _state.update { it.copy(showNotificationDialog = false, error = AppErrorMapper.map(e)) }
            }
        _state.update { it.copy(isSubmittingNotification = false) }
    }

    private fun handleMissingPermission() {
        if (notificationStorage.hasRequestedPermission()) {
            _state.update { it.copy(showNotificationSettingsDialog = true) }
        } else {
            notificationStorage.setRequestedPermission(true)
            navigateTo(FightDetailNavigationEvent.RequestNotificationPermission)
        }
    }

    private fun submitPrediction(predictedWinnerId: String, selectedRisk: Int) {
        viewModelScope.launch {
            val userId = authRepository.getAuthenticatedUserId()
            if (userId == null) {
                _state.update { 
                    it.copy(
                        error = AppError.UNAUTHENTICATED,
                        showPredictionDialog = false,
                        pendingPredictionFighterId = null
                    ) 
                }
                return@launch
            }

            val fight = _state.value.fight ?: return@launch
            if (isFightCompleted(fight)) {
                _state.update { 
                    it.copy(
                        error = AppError.FIGHT_OVER,
                        showPredictionDialog = false,
                        pendingPredictionFighterId = null
                    ) 
                }
                return@launch
            }

            if(!areOddsPublished(fight)){
                _state.update { 
                    it.copy(
                        error = AppError.ODDS_NOT_PUBLISHED,
                        showPredictionDialog = false,
                        pendingPredictionFighterId = null
                    ) 
                }
                return@launch
            }

            val lockedOdds = getLockedOdds(fight, predictedWinnerId)

            _state.update { it.copy(isSubmittingPrediction = true, error = null) }
            
            predictionRepository.addPrediction(userId, fight.fightId, predictedWinnerId, lockedOdds, selectedRisk)
                .onSuccess {
                    _state.update { 
                        it.copy(
                            isSubmittingPrediction = false,
                            showPredictionDialog = false,
                            pendingPredictionFighterId = null
                        ) 
                    }
                }
                .onFailure { e ->
                    _state.update { 
                        it.copy(
                            isSubmittingPrediction = false,
                            showPredictionDialog = false,
                            pendingPredictionFighterId = null,
                            error = AppErrorMapper.map(e)
                        ) 
                    }
                }
        }
    }

    private fun areOddsPublished(fight: Fight): Boolean {
        return fight.participants.all { it.oddsValue != null && it.oddsValue != 0 }
    }

    private fun getLockedOdds(fight: Fight, predictedWinnerId: String): Int {
        return fight.participants.find { it.fighter.fighterId == predictedWinnerId }?.oddsValue ?: 0
    }

    private fun onRefresh() {
        if (refreshJob?.isActive == true) return

        refreshJob = viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }

            fightRepository.syncFight(fightId)
                .onSuccess {
                    _state.update { it.copy(isRefreshing = false) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isRefreshing = false, error = AppErrorMapper.map(e)) }
                }
        }
    }

    private fun navigateTo(event: FightDetailNavigationEvent) {
        viewModelScope.launch {
            _navigation.emit(event)
        }
    }
}