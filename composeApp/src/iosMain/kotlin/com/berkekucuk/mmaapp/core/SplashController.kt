package com.berkekucuk.mmaapp.core

import com.berkekucuk.mmaapp.presentation.screens.home.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SplashController : KoinComponent {

    private val homeViewModel: HomeViewModel by inject()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun observeLoadingState(onLoadingFinished: () -> Unit) {
        scope.launch {
            homeViewModel.state
                .map { it.isLoading }
                .distinctUntilChanged()
                .collect { isLoading ->
                    if (!isLoading) {
                        onLoadingFinished()
                        scope.cancel()
                    }
                }
        }
    }

    fun dispose() {
        scope.cancel()
    }
}
