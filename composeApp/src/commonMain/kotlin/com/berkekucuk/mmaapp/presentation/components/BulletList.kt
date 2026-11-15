package com.berkekucuk.mmaapp.presentation.components

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BulletList(
    items: List<String>,
    modifier: Modifier = Modifier,
    color: Color = AppTheme.colors.textSecondary,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEach { item ->
            Row {
                Text(text = "•", color = color)
                Text(
                    text = item,
                    color = color,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
