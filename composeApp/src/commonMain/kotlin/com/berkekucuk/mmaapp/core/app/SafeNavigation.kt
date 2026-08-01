package com.berkekucuk.mmaapp.core.app

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import kotlin.time.TimeSource

private const val DEFAULT_NAVIGATION_THROTTLE_MS = 600L
private const val NAVIGATE_UP_KEY = "navigate_up"

class NavigationThrottle(private val throttleMs: Long = DEFAULT_NAVIGATION_THROTTLE_MS) {
    private val lastNavigations = mutableMapOf<Any, Long>()
    private val mark = TimeSource.Monotonic.markNow()

    fun shouldNavigate(key: Any): Boolean {
        val now = mark.elapsedNow().inWholeMilliseconds
        val last = lastNavigations[key]
        if (last != null && now - last < throttleMs) {
            return false
        }
        lastNavigations[key] = now
        prune(now)
        return true
    }

    private fun prune(now: Long) {
        val iterator = lastNavigations.entries.iterator()
        while (iterator.hasNext()) {
            if (now - iterator.next().value >= throttleMs) {
                iterator.remove()
            }
        }
    }
}

fun NavController.safeNavigate(
    throttle: NavigationThrottle,
    route: Any,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    if (throttle.shouldNavigate(route)) {
        navigate(route, builder)
    }
}

fun NavController.safeNavigateUp(throttle: NavigationThrottle): Boolean {
    if (!throttle.shouldNavigate(NAVIGATE_UP_KEY)) return false
    return navigateUp()
}
