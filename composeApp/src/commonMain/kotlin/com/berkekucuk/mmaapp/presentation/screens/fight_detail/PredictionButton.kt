package com.berkekucuk.mmaapp.presentation.screens.fight_detail

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.berkekucuk.mmaapp.domain.model.Fighter
import com.berkekucuk.mmaapp.presentation.components.FighterImage

@Composable
fun PredictionButton(
    fighter: Fighter,
    isSelected: Boolean,
    isSubmitting: Boolean,
    isOtherSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        isSelected -> AppTheme.colors.winnerFrame
        else -> AppTheme.colors.cardBorder
    }

    val alpha = if (isOtherSelected) 0.5f else 1f
    val isEnabled = !isSubmitting && !isSelected && !isOtherSelected

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .background(if (isSelected) AppTheme.colors.winnerFrame.copy(alpha = 0.1f) else Color.Transparent)
            .clickable(enabled = isEnabled) { onClick() }
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.alpha(alpha)) {
            FighterImage(
                imageUrl = fighter.imageUrl,
                name = fighter.name,
                countryCode = fighter.countryCode,
                alignment = Alignment.CenterHorizontally,
                imageSize = 28.dp,
                showFlag = false
            )
        }
    }
}