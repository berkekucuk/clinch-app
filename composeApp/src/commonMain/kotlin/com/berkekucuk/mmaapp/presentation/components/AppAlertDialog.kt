package com.berkekucuk.mmaapp.presentation.components

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable

@Composable
fun AppAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    onDismissClick: (() -> Unit)? = null,
    title: String? = null,
    text: String? = null,
    content: @Composable (() -> Unit)? = null,
    confirmText: String,
    dismissText: String? = null,
    isDestructive: Boolean = false,
    confirmEnabled: Boolean = true,
    isConfirmLoading: Boolean = false,
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = title?.let {
            {
                Text(
                    text = it,
                    color = AppTheme.colors.textPrimary
                )
            }
        },
        text = {
            if (content != null) {
                content()
            } else if (text != null) {
                Text(
                    text = text,
                    color = AppTheme.colors.textSecondary
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmClick,
                enabled = confirmEnabled && !isConfirmLoading
            ) {
                if (isConfirmLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp), 
                        strokeWidth = 2.dp, 
                        color = if (isDestructive) AppTheme.colors.ufcRed else AppTheme.colors.winnerFrame
                    )
                } else {
                    Text(
                        text = confirmText,
                        color = if (!confirmEnabled) AppTheme.colors.textSecondary.copy(alpha = 0.5f) else if (isDestructive) AppTheme.colors.ufcRed else AppTheme.colors.winnerFrame
                    )
                }
            }
        },
        dismissButton = dismissText?.let {
            {
                TextButton(onClick = onDismissClick ?: onDismissRequest) {
                    Text(text = it, color = AppTheme.colors.textSecondary)
                }
            }
        },
        containerColor = AppTheme.colors.fightItemBackground,
    )
}
