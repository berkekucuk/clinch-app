package com.berkekucuk.mmaapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ripple
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors

fun Modifier.appClickable(enabled: Boolean = true, onClick: () -> Unit): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val colors = LocalAppColors.current
    val indicationColor = if (colors.isDark) colors.white else colors.black

    this.clickable(
        interactionSource = interactionSource,
        indication = ripple(color = indicationColor),
        enabled = enabled,
        onClick = onClick
    )
}