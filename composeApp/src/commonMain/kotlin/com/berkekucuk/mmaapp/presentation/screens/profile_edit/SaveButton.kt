package com.berkekucuk.mmaapp.presentation.screens.profile_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors

@Composable
fun SaveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSaving: Boolean = false,
    enabled: Boolean = true,
) {
    val colors = LocalAppColors.current

    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isSaving,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    color = colors.winnerFrame,
                    strokeWidth = 2.dp,
                )
            }

            Text(
                text = text,
                color = colors.winnerFrame,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}