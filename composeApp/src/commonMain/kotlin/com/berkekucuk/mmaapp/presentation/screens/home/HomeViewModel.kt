package com.berkekucuk.mmaapp.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkekucuk.mmaapp.core.utils.AppErrorMapper
import com.berkekucuk.mmaapp.core.utils.DateTimeProvider
import com.berkekucuk.mmaapp.domain.model.AppUpdateStatus
import com.berkekucuk.mmaapp.domain.repository.EventRepository
import com.berkekucuk.mmaapp.domain.repository.AppVersionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class HomeViewModel(
    private val eventRepository: EventRepository,
    private val dateTimeProvider: DateTimeProvider,
    private val appVersionRepository: AppVersionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state = _state.asStateFlow()
    private val _navigation = MutableSharedFlow<HomeNavigationEvent>()
    val navigation = _navigation.asSharedFlow()
    private var isMinSplashTimePassed = false
    private var isDataReady = false

    init {
        val currentYear = dateTimeProvider.currentYear
        _state.update {
            it.copy(
                selectedYear = currentYear,
                availableYears = (1993..currentYear).toList().reversed(),
            )
        }

        viewModelScope.launch {
            delay(800.milliseconds)
            isMinSplashTimePassed = true
            checkLoadingState()
        }

        observeUpcomingEvents()
        observeCompletedEvents()
        syncEvents()
        checkForUpdate()
    }

    private fun checkLoadingState() {
        if (isMinSplashTimePassed && isDataReady) {
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun observeUpcomingEvents() {
        viewModelScope.launch {
            eventRepository.getUpcomingEvents()
                .catch {
                    isDataReady = true
                    checkLoadingState()
                }
                .collect { events ->
                    _state.update { it.copy(upcomingEvents = events) }
                    isDataReady = true
                    checkLoadingState()
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeCompletedEvents() {
        viewModelScope.launch {
            _state
                .map { it.selectedYear }
                .distinctUntilChanged()
                .flatMapLatest { year ->
                    eventRepository.getCompletedEventsByYear(year)
                }
                .collect { events ->
                    _state.update { it.copy(completedEvents = events) }
                }
        }
    }

    private fun syncEvents() {
        viewModelScope.launch {
            eventRepository.initializeEvents()
                .onFailure { e ->
                    if (isDataReady && _state.value.upcomingEvents.isEmpty()) {
                        _state.update { it.copy(error = AppErrorMapper.map(e)) }
                    }
                }
        }
    }

    private fun checkForUpdate() {
        viewModelScope.launch {
            appVersionRepository.checkForUpdate()
                .onSuccess { status ->
                    _state.update { it.copy(updateStatus = status) }
                }
                .onFailure {
                    _state.update { it.copy(updateStatus = AppUpdateStatus.UpToDate) }
                }
        }
    }

    fun onAction(action: HomeUiAction) {
        when (action) {
            is HomeUiAction.OnEventClicked -> navigateTo(HomeNavigationEvent.ToEventDetail(action.eventId))
            is HomeUiAction.OnYearSelected -> onYearSelected(action.year)
            is HomeUiAction.OnRefreshUpcomingTab -> onRefreshUpcomingTab()
            is HomeUiAction.OnRefreshCompletedTab -> onRefreshCompletedTab()
            is HomeUiAction.OnSearchClicked -> navigateTo(HomeNavigationEvent.ToFighterSearch)
            HomeUiAction.OnDismissFlexibleUpdate -> _state.update { it.copy(updateStatus = AppUpdateStatus.UpToDate) }
        }
    }

    private fun onYearSelected(year: Int) {
        if (_state.value.selectedYear == year) return
        _state.update { it.copy(selectedYear = year, isRefreshingCompletedTab = true, error = null) }
        viewModelScope.launch {
            eventRepository.syncEventsByYear(year)
                .onSuccess {
                    _state.update { it.copy(isRefreshingCompletedTab = false) }
                }
                .onFailure { e ->
                    val isSynced = eventRepository.isYearSynced(year)
                    val error = if (!isSynced) AppErrorMapper.map(e) else null
                    _state.update { it.copy(isRefreshingCompletedTab = false, error = error) }
                }
        }
    }

    private fun onRefreshUpcomingTab() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshingUpcomingTab = true, error = null) }
            eventRepository.syncPendingEvents()
                .onSuccess {
                    _state.update { it.copy(isRefreshingUpcomingTab = false) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isRefreshingUpcomingTab = false, error = AppErrorMapper.map(e)) }
                }
        }
    }

    private fun onRefreshCompletedTab() {
        val selectedYear = _state.value.selectedYear
        viewModelScope.launch {
            _state.update { it.copy(isRefreshingCompletedTab = true, error = null) }
            eventRepository.syncEventsByYear(selectedYear, forceRefresh = true)
                .onSuccess {
                    _state.update { it.copy(isRefreshingCompletedTab = false, error = null) }
                }
                .onFailure { e ->
                    val isSynced = eventRepository.isYearSynced(selectedYear)
                    val error = if (!isSynced) AppErrorMapper.map(e) else null
                    _state.update { it.copy(isRefreshingCompletedTab = false, error = error) }
                }
        }
    }

    private fun navigateTo(event: HomeNavigationEvent) {
        viewModelScope.launch {
            _navigation.emit(event)
        }
    }
}