package com.berkekucuk.mmaapp.presentation.screens.fight_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.berkekucuk.mmaapp.core.app.Route
import com.berkekucuk.mmaapp.core.utils.AppErrorMapper
import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.domain.model.AuthState
import com.berkekucuk.mmaapp.domain.repository.AuthRepository
import com.berkekucuk.mmaapp.domain.repository.FighterRepository
import com.berkekucuk.mmaapp.domain.repository.NotificationRepository
import com.berkekucuk.mmaapp.domain.repository.PredictionRepository
import com.berkekucuk.mmaapp.core.storage.NotificationStorage
import com.berkekucuk.mmaapp.domain.model.Fight
import com.berkekucuk.mmaapp.domain.model.Fighter
import com.berkekucuk.mmaapp.domain.repository.FightRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FightDetailViewModel(
    private val fightRepository: FightRepository,
    private val fighterRepository: FighterRepository,
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
    private var isPendingNotificationRequest = false
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
                    syncFighters(fight, knownFighterId = fighterId)
                }
        }
    }

    private fun observeFightNotificationStatus() {
        viewModelScope.launch {
            val userId = getAuthenticatedUserId()
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
            val userId = getAuthenticatedUserId()
            if (userId != null) {
                predictionRepository.getPredictedWinnerId(fightId, userId)
                    .collect { predictedId ->
                        _state.update { it.copy(predictedWinnerId = predictedId) }
                    }
            }
        }
    }

    private fun syncFighters(fight: Fight, knownFighterId: String? = null) {
        val redId = fight.redCorner?.fighter?.fighterId ?: return
        val blueId = fight.blueCorner?.fighter?.fighterId ?: return
        _state.update { it.copy(error = null) }
        loadFighter(redId, sync = redId != knownFighterId) { fighter ->
            _state.update { it.copy(redFighter = fighter) }
        }
        loadFighter(blueId, sync = blueId != knownFighterId) {
            fighter -> _state.update { it.copy(blueFighter = fighter) }
        }
    }

    private fun loadFighter(fighterId: String, sync: Boolean, onUpdate: (Fighter) -> Unit) {
        viewModelScope.launch {
            if (sync) {
                fighterRepository.syncFighter(fighterId)
                    .onFailure { e ->
                        _state.update { it.copy(error = AppErrorMapper.map(e)) }
                    }
            }

            val fighter = fighterRepository.getFighter(fighterId).first()
            onUpdate(fighter)
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
            is FightDetailUiAction.OnNotificationClicked -> onNotificationClicked()
            is FightDetailUiAction.OnErrorShown -> _state.update { it.copy(error = null) }
            is FightDetailUiAction.OnResume -> {
                if (isPendingNotificationRequest) {
                    viewModelScope.launch {
                        if (notificationStorage.load()) {
                            isPendingNotificationRequest = false
                            onNotificationClicked()
                        }
                    }
                }
            }
            is FightDetailUiAction.OnSubmitPredictionClicked -> submitPrediction(action.predictedWinnerId, action.selectedRisk)
            is FightDetailUiAction.OnLeaderboardClicked -> navigateTo(FightDetailNavigationEvent.ToLeaderboard)
            is FightDetailUiAction.OnOpenSettingsClicked -> {
                notificationStorage.openNotificationSettings()
            }
            is FightDetailUiAction.OnPredictClicked -> {
                _state.update { it.copy(showPredictionConfirmDialog = true, pendingPredictionFighterId = action.predictedWinnerId) }
            }
            is FightDetailUiAction.OnDismissPredictionDialog -> {
                _state.update { it.copy(showPredictionConfirmDialog = false, pendingPredictionFighterId = null) }
            }
        }
    }

    private fun onNotificationClicked() {
        viewModelScope.launch {
            val userId = getAuthenticatedUserId()
            if (userId == null) {
                _state.update { it.copy(error = AppError.UNAUTHENTICATED) }
                return@launch
            }

            val fight = _state.value.fight ?: return@launch
            val isNotificationEnabled = _state.value.isNotificationEnabled
            if (!canToggleNotification(fight, isNotificationEnabled)) return@launch

            if (isNotificationEnabled) {
                removeNotification(fight.fightId, userId)
            } else {
                enableNotificationWithPermissionCheck(fight.fightId, userId)
            }
        }
    }

    private fun isFightCompleted(fight: Fight): Boolean {
        return fight.methodType.isNotBlank() || fight.methodDetail.isNotBlank()
    }

    private fun canToggleNotification(fight: Fight, isNotificationEnabled: Boolean): Boolean {
        if (isFightCompleted(fight) && !isNotificationEnabled) {
            _state.update { it.copy(error = AppError.FIGHT_OVER) }
            return false
        }
        return true
    }

    private suspend fun removeNotification(fightId: String, userId: String) {
        notificationRepository.removeFightNotification(fightId, userId)
            .onFailure { e ->
                _state.update { it.copy(error = AppErrorMapper.map(e)) }
            }
    }

    private suspend fun enableNotificationWithPermissionCheck(fightId: String, userId: String) {
        if (!notificationStorage.load()) {
            handleMissingPermission()
            return
        }

        notificationRepository.addFightNotification(fightId, userId)
            .onFailure { e ->
            _state.update { it.copy(error = AppErrorMapper.map(e)) }
        }
    }

    private fun handleMissingPermission() {
        isPendingNotificationRequest = true
        if (notificationStorage.hasRequestedPermission()) {
            navigateTo(FightDetailNavigationEvent.ShowSettingsDialog)
        } else {
            notificationStorage.setRequestedPermission(true)
            navigateTo(FightDetailNavigationEvent.RequestNotificationPermission)
        }
    }

    private fun submitPrediction(predictedWinnerId: String, selectedRisk: Int) {
        viewModelScope.launch {
            val userId = getAuthenticatedUserId()
            if (userId == null) {
                _state.update { it.copy(error = AppError.UNAUTHENTICATED) }
                return@launch
            }

            val fight = _state.value.fight ?: return@launch
            if (isFightCompleted(fight)) {
                _state.update { it.copy(error = AppError.FIGHT_OVER) }
                return@launch
            }

            if(!areOddsPublished(fight)){
                _state.update { it.copy(error = AppError.ODDS_NOT_PUBLISHED) }
                return@launch
            }

            val lockedOdds = getLockedOdds(fight, predictedWinnerId)

            _state.update { it.copy(isSubmittingPrediction = true, error = null) }
            
            predictionRepository.addPrediction(userId, fight.fightId, predictedWinnerId, lockedOdds, selectedRisk)
                .onSuccess {
                    _state.update { 
                        it.copy(
                            isSubmittingPrediction = false,
                            showPredictionConfirmDialog = false,
                            pendingPredictionFighterId = null
                        ) 
                    }
                }
                .onFailure { e ->
                    _state.update { 
                        it.copy(
                            isSubmittingPrediction = false,
                            showPredictionConfirmDialog = false,
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

    private suspend fun getAuthenticatedUserId(): String? {
        val authState = authRepository.authState.first { it !is AuthState.Loading }
        return if (authState is AuthState.Authenticated) authState.userId else null
    }

    private fun onRefresh() {
        if (refreshJob?.isActive == true) return

        refreshJob = viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }

            fightRepository.syncFight(fightId)
                .onSuccess {
                _state.value.fight?.let { currentFight ->
                    syncFighters(currentFight, knownFighterId = fighterId)
                }
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