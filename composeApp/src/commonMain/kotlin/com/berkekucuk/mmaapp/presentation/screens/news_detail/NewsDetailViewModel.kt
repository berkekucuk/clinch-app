package com.berkekucuk.mmaapp.presentation.screens.news_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.berkekucuk.mmaapp.core.app.Route
import com.berkekucuk.mmaapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewsDetailViewModel(
    private val repository: NewsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val articleId = savedStateHandle.toRoute<Route.NewsDetail>().articleId

    private val _state = MutableStateFlow(NewsDetailUiState())
    val state: StateFlow<NewsDetailUiState> = _state.asStateFlow()
    private val _navigation = MutableSharedFlow<NewsDetailNavigationEvent>()
    val navigation = _navigation.asSharedFlow()

    init {
        observeArticle()
    }

    private fun observeArticle() {
        viewModelScope.launch {
            repository.getArticle(articleId)
                .collect { article ->
                    _state.update { it.copy(article = article) }
                }
        }
    }

    fun onAction(action: NewsDetailUiAction) {
        when (action) {
            is NewsDetailUiAction.OnBackClicked -> navigateTo(NewsDetailNavigationEvent.Back)
            is NewsDetailUiAction.OnRefresh -> syncArticle()
        }
    }

    private fun syncArticle() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            repository.syncArticle(articleId)
                .onSuccess {
                    _state.update { it.copy(isRefreshing = false) }
                }
                .onFailure {
                    _state.update { it.copy(isRefreshing = false) }
                }
        }
    }

    private fun navigateTo(event: NewsDetailNavigationEvent) {
        viewModelScope.launch {
            _navigation.emit(event)
        }
    }
}
