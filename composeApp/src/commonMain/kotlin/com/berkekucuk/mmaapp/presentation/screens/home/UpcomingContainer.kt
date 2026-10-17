package com.berkekucuk.mmaapp.presentation.screens.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.berkekucuk.mmaapp.domain.model.Event
import com.berkekucuk.mmaapp.presentation.components.ListContainer

@Composable
fun UpcomingContainer(
    events: List<Event>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onEventClick: (String) -> Unit,
    listState: LazyListState
) {
    ListContainer(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        listState = listState,
        contentPadding = PaddingValues(top = 16.dp)
    ) {
        items(
            items = events,
            key = { it.eventId },
            contentType = { "EventItem" }
        ) { event ->
            EventItem(
                event = event,
                onClick = onEventClick
            )
        }
    }
}