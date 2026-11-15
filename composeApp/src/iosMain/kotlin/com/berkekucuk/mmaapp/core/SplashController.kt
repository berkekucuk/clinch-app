package com.berkekucuk.mmaapp.core

import com.berkekucuk.mmaapp.core.app.AppStartupState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds

class SplashController {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun observeLoadingState(onLoadingFinished: () -> Unit) {
        scope.launch {
            withTimeoutOrNull(AppStartupState.MAX_SPLASH_DURATION_MS.milliseconds) {
                AppStartupState.isReady.first { it }
            }
            onLoadingFinished()
            scope.cancel()
        }
    }

    fun dispose() {
        scope.cancel()
    }
}
