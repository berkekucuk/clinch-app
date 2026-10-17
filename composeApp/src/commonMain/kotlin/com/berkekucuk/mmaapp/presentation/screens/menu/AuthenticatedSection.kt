package com.berkekucuk.mmaapp.presentation.screens.menu

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berkekucuk.mmaapp.presentation.components.FighterPortrait

@Composable
fun AuthenticatedSection(
    name: String,
    username: String?,
    avatarUrl: String?,
    onProfileClick: () -> Unit,
    onProfileEditClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(color = AppTheme.colors.white),
                    onClick = onProfileClick
                )
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            FighterPortrait(
                name = name,
                imageUrl = avatarUrl,
                countryCode = null,
                record = username?.let { "@$it" },
                alignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                nameFontSize = 14.sp,
                flagWidth = 0.dp,
                flagHeight = 0.dp,
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = AppTheme.colors.textSecondary,
                modifier = Modifier.size(24.dp)
            )
        }

        HorizontalDivider(color = AppTheme.colors.dividerColor)

        MenuItemRow(
            icon = Icons.Filled.Edit,
            title = AppTheme.strings.menuProfileSettings,
            onClick = onProfileEditClick,
        )
    }
}
