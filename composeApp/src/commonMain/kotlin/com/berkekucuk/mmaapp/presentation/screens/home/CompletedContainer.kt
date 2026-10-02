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
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

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
    val dropdownScrollState = rememberScrollState()

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
                        scrollState = dropdownScrollState,
                        modifier = Modifier
                            .heightIn(max = 300.dp)
                            .simpleVerticalScrollbar(
                                state = dropdownScrollState,
                                color = colors.textSecondary.copy(alpha = 0.5f),
                                trackColor = colors.dividerColor.copy(alpha = 0.3f),
                            ),
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

private fun Modifier.simpleVerticalScrollbar(
    state: ScrollState,
    color: Color,
    trackColor: Color = Color.Transparent,
    width: androidx.compose.ui.unit.Dp = 3.dp,
    padding: androidx.compose.ui.unit.Dp = 3.dp,
): Modifier = drawWithContent {
    drawContent()
    if (state.maxValue > 0) {
        val totalHeight = size.height
        val contentHeight = totalHeight + state.maxValue
        val scrollbarHeight = ((totalHeight / contentHeight) * totalHeight).coerceAtLeast(24.dp.toPx())
        val scrollbarOffsetY = (state.value.toFloat() / state.maxValue) * (totalHeight - scrollbarHeight)

        val paddingPx = padding.toPx()
        val widthPx = width.toPx()

        if (trackColor != Color.Transparent) {
            drawRoundRect(
                color = trackColor,
                topLeft = Offset(size.width - widthPx - paddingPx, 0f),
                size = Size(widthPx, totalHeight),
                cornerRadius = CornerRadius(widthPx / 2, widthPx / 2)
            )
        }

        drawRoundRect(
            color = color,
            topLeft = Offset(size.width - widthPx - paddingPx, scrollbarOffsetY),
            size = Size(widthPx, scrollbarHeight),
            cornerRadius = CornerRadius(widthPx / 2, widthPx / 2)
        )
    }
}
