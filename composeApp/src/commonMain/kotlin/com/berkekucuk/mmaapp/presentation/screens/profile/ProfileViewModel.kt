package com.berkekucuk.mmaapp.presentation.screens.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.berkekucuk.mmaapp.core.app.Route
import com.berkekucuk.mmaapp.core.utils.AppError
import com.berkekucuk.mmaapp.core.utils.AppErrorMapper
import com.berkekucuk.mmaapp.domain.enums.BanAction
import com.berkekucuk.mmaapp.domain.enums.ProfileSuccessMessage
import com.berkekucuk.mmaapp.domain.model.AuthState
import com.berkekucuk.mmaapp.domain.repository.AuthRepository
import com.berkekucuk.mmaapp.domain.repository.InteractionRepository
import com.berkekucuk.mmaapp.domain.repository.PredictionRepository
import com.berkekucuk.mmaapp.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.async

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val predictionRepository: PredictionRepository,
    private val interactionRepository: InteractionRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<Route.Profile>()
    private val userId: String = route.userId

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private val _navigation = MutableSharedFlow<ProfileNavigationEvent>()
    val navigation = _navigation.asSharedFlow()

    private val currentPageFlow = MutableStateFlow(0)

    private var syncJob: Job? = null

    init {
        observeModerationRole()
        observeProfile()
        observePredictions()
        syncProfile()
    }

    private fun observeModerationRole() {
        viewModelScope.launch {
            authRepository.authState
                .map { (it as? AuthState.Authenticated)?.role?.canModerate == true }
                .distinctUntilChanged()
                .collect { canModerate ->
                    _state.update { it.copy(canModerate = canModerate) }
                }
        }
    }

    private fun observeProfile() {
        viewModelScope.launch {
            val currentUserId = authRepository.getAuthenticatedUserId()
            _state.update {
                it.copy(isOwner = currentUserId == userId)
            }

            userRepository.getUserProfile(userId)
                .collect { profile ->
                    _state.update {
                        it.copy(profile = profile, isLoading = false)
                    }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observePredictions() {
        viewModelScope.launch {
            currentPageFlow
                .flatMapLatest { page ->
                    predictionRepository.getPredictions(userId, limit = 20, offset = page * 20)
                }
                .collect { predictions ->
                    _state.update {
                        it.copy(
                            predictions = predictions,
                            canGoNext = predictions.size == 20
                        )
                    }
                }
        }
    }

    private fun syncProfile(isRefreshing: Boolean = false, onlyPredictions: Boolean = false) {
        if (syncJob?.isActive == true) return

        syncJob = viewModelScope.launch {
            _state.update { it.copy(isRefreshing = isRefreshing, error = null) }

            val page = currentPageFlow.value

            if (onlyPredictions) {
                syncPredictions(page)
                    .onFailure { e -> _state.update { it.copy(error = AppErrorMapper.map(e)) } }
            } else {
                userRepository.syncUser(userId)
                    .onFailure { e ->
                        _state.update { it.copy(isRefreshing = false, error = AppErrorMapper.map(e)) }
                        return@launch
                    }

                val interactionDeferred = async { interactionRepository.syncInteractions(userId) }
                val predictionDeferred = async { syncPredictions(page) }

                val interactionResult = interactionDeferred.await()
                val predictionResult = predictionDeferred.await()

                val firstError = listOfNotNull(predictionResult, interactionResult)
                    .firstNotNullOfOrNull { it.exceptionOrNull() }

                if (firstError != null) {
                    _state.update { it.copy(error = AppErrorMapper.map(firstError)) }
                }
            }

            _state.update { it.copy(isRefreshing = false) }
        }
    }

    private suspend fun syncPredictions(page: Int): Result<Unit> {
        return predictionRepository.syncPredictions(userId, limit = 20, offset = page * 20)
    }

    fun onAction(action: ProfileUiAction) {
        when (action) {
            is ProfileUiAction.OnBackClicked -> navigateTo(ProfileNavigationEvent.Back)
            is ProfileUiAction.OnRefresh -> syncProfile(isRefreshing = true)
            is ProfileUiAction.OnInteractionListClicked -> navigateTo(ProfileNavigationEvent.ToInteractionList(userId, action.type))
            is ProfileUiAction.OnPredictionClicked -> navigateTo(ProfileNavigationEvent.ToFightDetail(action.fightId))
            is ProfileUiAction.OnMessageShown -> _state.update { it.copy(error = null, successMessage = null) }
            
            is ProfileUiAction.OnReportClicked -> _state.update { it.copy(showReportDialog = true) }
            is ProfileUiAction.OnReportReasonChanged -> _state.update { it.copy(reportReason = action.reason) }
            is ProfileUiAction.OnDismissReportDialog -> _state.update { it.copy(showReportDialog = false, reportReason = null) }
            is ProfileUiAction.OnSubmitReport -> reportUser()

            is ProfileUiAction.OnBlockClicked -> _state.update { it.copy(showBlockDialog = true) }
            is ProfileUiAction.OnDismissBlockDialog -> _state.update { it.copy(showBlockDialog = false) }
            is ProfileUiAction.OnConfirmBlock -> blockUser()

            is ProfileUiAction.OnBanClicked -> _state.update { it.copy(pendingBanAction = BanAction.BAN) }
            is ProfileUiAction.OnUnbanClicked -> _state.update { it.copy(pendingBanAction = BanAction.UNBAN) }
            is ProfileUiAction.OnDismissBanDialog -> _state.update { it.copy(pendingBanAction = null) }
            is ProfileUiAction.OnConfirmBan -> submitBan()

            is ProfileUiAction.OnNextPage -> nextPage()
            is ProfileUiAction.OnPreviousPage -> previousPage()
        }
    }

    private fun nextPage() {
        val currentState = _state.value
        if (!currentState.canGoNext || currentState.isRefreshing || syncJob?.isActive == true) return

        val nextPage = currentPageFlow.value + 1

        _state.update {
            it.copy(
                currentPage = nextPage,
                canGoNext = false
            )
        }

        currentPageFlow.value = nextPage
        syncProfile(isRefreshing = true, onlyPredictions = true)
    }

    private fun previousPage() {
        val currentState = _state.value
        if (currentPageFlow.value <= 0 || currentState.isRefreshing || syncJob?.isActive == true) return

        val prevPage = currentPageFlow.value - 1

        _state.update {
            it.copy(
                currentPage = prevPage,
                canGoNext = false
            )
        }

        currentPageFlow.value = prevPage
    }

    private fun reportUser() {
        val reasonEnum = _state.value.reportReason ?: return
        val reasonDbValue = reasonEnum.dbValue

        viewModelScope.launch {
            _state.update { it.copy(isReporting = true, error = null) }
            userRepository.reportUser(userId, reasonDbValue)
                .onSuccess {
                    _state.update {
                        it.copy(isReporting = false, showReportDialog = false, reportReason = null)
                    }
                }
                .onFailure { e ->
                    _state.update { 
                        it.copy(isReporting = false, showReportDialog = false, reportReason = null, error = AppErrorMapper.map(e))
                    }
                }
        }
    }

    private fun blockUser() {
        viewModelScope.launch {
            _state.update { it.copy(showBlockDialog = false, error = null) }
            val currentUserId = authRepository.getAuthenticatedUserId()
            if (currentUserId == null) {
                _state.update { it.copy(error = AppError.UNAUTHENTICATED) }
                return@launch
            }

            userRepository.blockUser(currentUserId, userId)
                .onSuccess {
                    navigateTo(ProfileNavigationEvent.Back)
                }
                .onFailure { e ->
                    _state.update { it.copy(error = AppErrorMapper.map(e)) }
                }
        }
    }

    private fun submitBan() {
        val action = _state.value.pendingBanAction ?: return

        viewModelScope.launch {
            _state.update { it.copy(isSubmittingBan = true, error = null) }
            val result = when (action) {
                BanAction.BAN -> userRepository.banUserFromComments(userId)
                BanAction.UNBAN -> userRepository.unbanUserFromComments(userId)
            }
            result
                .onSuccess {
                    val message = when (action) {
                        BanAction.BAN -> ProfileSuccessMessage.USER_BANNED
                        BanAction.UNBAN -> ProfileSuccessMessage.USER_UNBANNED
                    }
                    _state.update { it.copy(isSubmittingBan = false, pendingBanAction = null, successMessage = message) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isSubmittingBan = false, pendingBanAction = null, error = AppErrorMapper.map(e)) }
                }
        }
    }

    private fun navigateTo(event: ProfileNavigationEvent) {
        viewModelScope.launch {
            _navigation.emit(event)
        }
    }
}