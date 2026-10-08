package com.berkekucuk.mmaapp.presentation.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun AppSnackbarEffect(
    message: String?,
    snackbarHostState: SnackbarHostState,
    duration: SnackbarDuration = SnackbarDuration.Indefinite,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
) {
    LaunchedEffect(message) {
        if (message == null) return@LaunchedEffect
        while (true) {
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = duration,
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    onAction?.invoke()
                    break
                }
                SnackbarResult.Dismissed -> {
                    if (onDismiss != null) {
                        onDismiss.invoke()
                        break
                    }
                }
            }
        }
    }
}
