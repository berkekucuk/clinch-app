package com.berkekucuk.mmaapp.presentation.screens.news_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.berkekucuk.mmaapp.core.app.Route
import com.berkekucuk.mmaapp.core.utils.AppErrorMapper
import com.berkekucuk.mmaapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewsEditViewModel(
    private val repository: NewsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val articleId = savedStateHandle.toRoute<Route.NewsEdit>().articleId

    private val _state = MutableStateFlow(NewsEditUiState())
    val state: StateFlow<NewsEditUiState> = _state.asStateFlow()
    private val _navigation = MutableSharedFlow<NewsEditNavigationEvent>()
    val navigation = _navigation.asSharedFlow()

    init {
        loadArticle()
    }

    private fun loadArticle() {
        viewModelScope.launch {
            val article = repository.getArticle(articleId).filterNotNull().first()
            _state.update {
                it.copy(
                    isLoaded = true,
                    titleTr = article.titleTr,
                    summaryTr = article.summaryTr,
                    titleEn = article.titleEn,
                    summaryEn = article.summaryEn,
                )
            }
        }
    }

    fun onAction(action: NewsEditUiAction) {
        when (action) {
            is NewsEditUiAction.OnBackClicked -> navigateTo(NewsEditNavigationEvent.Back)
            is NewsEditUiAction.OnTitleTrChanged -> _state.update { it.copy(titleTr = action.text) }
            is NewsEditUiAction.OnSummaryTrChanged -> _state.update { it.copy(summaryTr = action.text) }
            is NewsEditUiAction.OnTitleEnChanged -> _state.update { it.copy(titleEn = action.text) }
            is NewsEditUiAction.OnSummaryEnChanged -> _state.update { it.copy(summaryEn = action.text) }
            is NewsEditUiAction.OnSaveClicked -> save()
            is NewsEditUiAction.OnMessageShown -> _state.update { it.copy(error = null) }
        }
    }

    private fun save() {
        val currentState = _state.value
        if (!currentState.canSave) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            repository.updateArticle(
                id = articleId,
                titleTr = currentState.titleTr.trim(),
                summaryTr = currentState.summaryTr.trim(),
                titleEn = currentState.titleEn.trim(),
                summaryEn = currentState.summaryEn.trim(),
            )
                .onSuccess {
                    _state.update { it.copy(isSaving = false) }
                    _navigation.emit(NewsEditNavigationEvent.Back)
                }
                .onFailure { e ->
                    _state.update { it.copy(isSaving = false, error = AppErrorMapper.map(e)) }
                }
        }
    }

    private fun navigateTo(event: NewsEditNavigationEvent) {
        viewModelScope.launch {
            _navigation.emit(event)
        }
    }
}
