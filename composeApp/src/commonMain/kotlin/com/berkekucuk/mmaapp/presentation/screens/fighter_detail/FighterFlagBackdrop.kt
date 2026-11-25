package com.berkekucuk.mmaapp.presentation.screens.fighter_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.berkekucuk.mmaapp.core.presentation.AppTheme
import com.berkekucuk.mmaapp.presentation.components.rememberFlagImageRequest

private const val SCRIM_ALPHA = 0.75f

@Composable
fun FighterFlagBackdrop(
    countryCode: String?,
    modifier: Modifier = Modifier,
) {
    val request = rememberFlagImageRequest(countryCode) ?: return

    Box(modifier = modifier) {
        AsyncImage(
            model = request,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.High,
            modifier = Modifier
                .matchParentSize()
                .blur(8.dp, BlurredEdgeTreatment.Rectangle),
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(AppTheme.colors.eventsTopBar, alpha = SCRIM_ALPHA)
        )
    }
}
