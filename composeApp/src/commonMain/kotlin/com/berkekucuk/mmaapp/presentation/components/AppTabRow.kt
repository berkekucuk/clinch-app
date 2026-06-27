package com.berkekucuk.mmaapp.presentation.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    containerColor: Color = LocalAppColors.current.topBarBackground,
) {
    val colors = LocalAppColors.current

    PrimaryTabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = containerColor,
        contentColor = colors.textPrimary,
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedTabIndex),
                width = Dp.Unspecified,
                height = 2.dp,
                color = colors.ufcRed
            )
        },
        divider = {}
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                selectedContentColor = colors.textPrimary,
                unselectedContentColor = colors.textSecondary
            )
        }
    }
}
