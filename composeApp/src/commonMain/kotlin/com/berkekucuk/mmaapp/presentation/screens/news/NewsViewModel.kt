package com.berkekucuk.mmaapp.presentation.screens.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkekucuk.mmaapp.core.utils.AppErrorMapper
import com.berkekucuk.mmaapp.domain.repository.NewsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewsViewModel(
    private val repository: NewsRepository,
) : ViewModel() {

    companion object {
        const val PAGE_SIZE = 20
    }

    private val _state = MutableStateFlow(NewsUiState())
    val state: StateFlow<NewsUiState> = _state.asStateFlow()
    private val _navigation = MutableSharedFlow<NewsNavigationEvent>()
    val navigation = _navigation.asSharedFlow()

    private val currentPageFlow = MutableStateFlow(0)

    private var syncJob: Job? = null

    init {
        observeArticles()
        syncArticles()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeArticles() {
        viewModelScope.launch {
            currentPageFlow
                .flatMapLatest { page ->
                    repository.getArticles(limit = PAGE_SIZE, offset = page * PAGE_SIZE)
                }
                .collect { articles ->
                    _state.update {
                        it.copy(
                            articles = articles,
                            canGoNext = articles.size == PAGE_SIZE
                        )
                    }
                }
        }
    }

    private fun syncArticles(isRefreshing: Boolean = false) {
        if (syncJob?.isActive == true) return

        syncJob = viewModelScope.launch {
            _state.update { it.copy(isRefreshing = isRefreshing, error = null) }

            val page = currentPageFlow.value

            repository.syncArticles(limit = PAGE_SIZE, offset = page * PAGE_SIZE)
                .onFailure { e -> _state.update { it.copy(error = AppErrorMapper.map(e)) } }

            _state.update { it.copy(isRefreshing = false) }
        }
    }

    fun onAction(action: NewsUiAction) {
        when (action) {
            is NewsUiAction.OnRefresh -> syncArticles(isRefreshing = true)
            is NewsUiAction.OnNextPage -> nextPage()
            is NewsUiAction.OnPreviousPage -> previousPage()
            is NewsUiAction.OnArticleClicked -> navigateTo(NewsNavigationEvent.ToNewsDetail(action.articleId))
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
        syncArticles(isRefreshing = true)
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

    private fun navigateTo(event: NewsNavigationEvent) {
        viewModelScope.launch {
            _navigation.emit(event)
        }
    }
}
