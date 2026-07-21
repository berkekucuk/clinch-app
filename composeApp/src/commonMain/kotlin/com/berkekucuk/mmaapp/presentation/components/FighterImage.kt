package com.berkekucuk.mmaapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings

@Composable
fun FighterImage(
    imageUrl: String,
    name: String,
    countryCode: String,
    result: String? = null,
    alignment: Alignment.Horizontal,
    modifier: Modifier = Modifier,
    imageSize: Dp = 55.dp,
    flagWidth: Dp = 18.dp,
    flagHeight: Dp = 12.dp,
    showFlag: Boolean = true,
) {
    val context = LocalPlatformContext.current
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current

    val boxAlignment = if (alignment == Alignment.Start) Alignment.BottomStart else Alignment.BottomEnd
    val badgeAlignment = if (alignment == Alignment.Start) Alignment.BottomEnd else Alignment.BottomStart

    val isWinner = result?.equals("WIN", ignoreCase = true) == true
    val borderModifier = if (isWinner) Modifier.border(1.5.dp, colors.winnerFrame, CircleShape) else Modifier

    val imageRequest = remember(imageUrl) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .size(200, 200)
            .memoryCacheKey(imageUrl)
            .build()
    }

    val flagUrl = remember(countryCode) {
        if (countryCode.isNotBlank()) {
            "https://d18vwyi4kwpgwa.cloudfront.net/flags/${countryCode.lowercase()}.png"
        } else null
    }

    val flagRequest = remember(flagUrl) {
        flagUrl?.let {
            ImageRequest.Builder(context)
                .data(it)
                .crossfade(true)
                .size(100, 100)
                .memoryCacheKey(it)
                .build()
        }
    }

    Box(modifier = modifier) {
        AsyncImage(
            model = imageRequest,
            contentDescription = name,
            modifier = Modifier
                .size(imageSize)
                .clip(CircleShape)
                .then(borderModifier),
            contentScale = ContentScale.Crop,
        )

        if (showFlag && flagRequest != null) {
            AsyncImage(
                model = flagRequest,
                contentDescription = strings.contentDescriptionFlag,
                modifier = Modifier
                    .align(boxAlignment)
                    .size(width = flagWidth, height = flagHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .border(0.5.dp, colors.dropdownMenuBackground, RoundedCornerShape(2.dp)),
                contentScale = ContentScale.Crop,
            )
        }

        if (isWinner) {
            val badgeSize = 15.dp
            val badgeOffset = (badgeSize - flagHeight) / 2
            Box(
                modifier = Modifier
                    .align(badgeAlignment)
                    .offset(y = badgeOffset)
                    .size(badgeSize)
                    .background(colors.winnerFrame, CircleShape)
                    .border(1.dp, colors.topBarBackground, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(10.dp),
                )
            }
        }
    }
}
