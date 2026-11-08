package com.berkekucuk.mmaapp.presentation.screens.event_detail

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlin.math.roundToInt

@Composable
fun EventPosterHeader(
    posterUrl: String,
    posterAreaHeight: Dp,
    collapseOffsetPx: () -> Float,
    tabRow: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
) {
    val context = LocalPlatformContext.current
    val posterShape = RoundedCornerShape(8.dp)

    val imageRequest = remember(posterUrl) {
        ImageRequest.Builder(context)
            .data(posterUrl)
            .crossfade(true)
            .build()
    }

    Box(modifier = modifier.fillMaxWidth()) {
        AsyncImage(
            model = imageRequest,
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .blur(24.dp),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        Column {
            topBar()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clipToBounds()
                    .layout { measurable, constraints ->
                        val maxHeight = posterAreaHeight.roundToPx()
                        val placeable = measurable.measure(
                            constraints.copy(minHeight = maxHeight, maxHeight = maxHeight)
                        )
                        val visibleHeight = (maxHeight + collapseOffsetPx()).roundToInt().coerceIn(0, maxHeight)
                        layout(placeable.width, visibleHeight) {
                            placeable.place(0, visibleHeight - maxHeight)
                        }
                    }
                    .graphicsLayer {
                        val maxHeight = posterAreaHeight.toPx()
                        alpha = ((maxHeight + collapseOffsetPx()) / maxHeight).coerceIn(0f, 1f)
                    },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageRequest,
                    contentDescription = AppTheme.strings.contentDescriptionEventPoster,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .height(posterAreaHeight - 16.dp)
                        .aspectRatio(4f / 5f)
                        .shadow(elevation = 12.dp, shape = posterShape)
                        .clip(posterShape),
                    contentScale = ContentScale.Crop,
                )
            }

            tabRow()
        }
    }
}
