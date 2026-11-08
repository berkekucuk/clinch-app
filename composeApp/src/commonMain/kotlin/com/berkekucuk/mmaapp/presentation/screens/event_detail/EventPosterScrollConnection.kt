package com.berkekucuk.mmaapp.presentation.screens.event_detail

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity

class EventPosterScrollConnection(
    private val posterAreaHeightPx: Float,
    private val collapseOffsetState: MutableFloatState,
    private val topBarConnection: NestedScrollConnection,
    private val isListAtTop: () -> Boolean,
) : NestedScrollConnection {

    val collapseOffsetPx: Float
        get() = collapseOffsetState.floatValue

    private fun moveBy(delta: Float): Offset {
        val newOffset = (collapseOffsetPx + delta).coerceIn(-posterAreaHeightPx, 0f)
        val consumed = newOffset - collapseOffsetPx
        collapseOffsetState.floatValue = newOffset
        return Offset(0f, consumed)
    }

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        return when {
            available.y < 0 -> {
                val posterConsumed = moveBy(available.y)
                posterConsumed + topBarConnection.onPreScroll(available - posterConsumed, source)
            }
            available.y > 0 -> {
                val topBarConsumed = topBarConnection.onPreScroll(available, source)
                val remaining = available - topBarConsumed
                if (remaining.y > 0 && source == NestedScrollSource.UserInput && isListAtTop()) {
                    topBarConsumed + moveBy(remaining.y)
                } else {
                    topBarConsumed
                }
            }
            else -> Offset.Zero
        }
    }

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
        val topBarConsumed = topBarConnection.onPostScroll(consumed, available, source)
        val remaining = available - topBarConsumed
        return if (remaining.y > 0 && source == NestedScrollSource.SideEffect) {
            topBarConsumed + moveBy(remaining.y)
        } else {
            topBarConsumed
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity =
        topBarConnection.onPreFling(available)

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity =
        topBarConnection.onPostFling(consumed, available)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberEventPosterScrollConnection(
    posterAreaHeight: Dp,
    topBarScrollBehavior: TopAppBarScrollBehavior,
    isListAtTop: () -> Boolean,
): EventPosterScrollConnection {
    val posterAreaHeightPx = with(LocalDensity.current) { posterAreaHeight.toPx() }
    val collapseOffsetState = rememberSaveable { mutableFloatStateOf(0f) }
    val currentIsListAtTop by rememberUpdatedState(isListAtTop)

    return remember(posterAreaHeightPx, topBarScrollBehavior) {
        EventPosterScrollConnection(
            posterAreaHeightPx = posterAreaHeightPx,
            collapseOffsetState = collapseOffsetState,
            topBarConnection = topBarScrollBehavior.nestedScrollConnection,
            isListAtTop = { currentIsListAtTop() },
        )
    }
}
