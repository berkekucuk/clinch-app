package com.berkekucuk.mmaapp.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
import com.berkekucuk.mmaapp.domain.model.Event
import com.berkekucuk.mmaapp.presentation.components.ListContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletedContainer(
    completedEvents: List<Event>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onEventClick: (String) -> Unit,
    availableYears: List<Int>,
    selectedYear: Int?,
    onYearSelected: (Int) -> Unit,
    listState: LazyListState
) {
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current
    var showDropdownMenu by remember { mutableStateOf(false) }

    ListContainer(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        listState = listState,
        contentPadding = PaddingValues(top = 8.dp)
    ) {
        item(key = "year_filter_row") {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenuBox(
                    expanded = showDropdownMenu,
                    onExpandedChange = { showDropdownMenu = it }
                ) {
                    FilterChip(
                        selected = true,
                        onClick = {},
                        label = {
                            Text(
                                text = selectedYear?.toString()
                                    ?: strings.selectYear,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDropdownMenu)
                        },
                        modifier = Modifier.menuAnchor(
                            ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                            true
                        ),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.dropdownMenuBackground,
                            selectedLabelColor = colors.textPrimary,
                            selectedTrailingIconColor = colors.textPrimary
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = showDropdownMenu,
                        onDismissRequest = { showDropdownMenu = false },
                        modifier = Modifier.heightIn(max = 300.dp),
                        containerColor = colors.dropdownMenuBackground
                    ) {
                        availableYears.forEach { year ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = year.toString(),
                                        color = colors.textPrimary
                                    )
                                },
                                onClick = {
                                    onYearSelected(year)
                                    showDropdownMenu = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }
        }

        items(
            items = completedEvents,
            key = { it.eventId },
            contentType = { "EventItem" }
        ) { event ->
            EventItem(
                event = event,
                onClick = { onEventClick(event.eventId) }
            )
        }
    }
}

