package com.berkekucuk.mmaapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FighterPortrait(
    name: String?,
    imageUrl: String?,
    countryCode: String?,
    result: String? = null,
    record: String?,
    alignment: Alignment.Horizontal,
    modifier: Modifier = Modifier,
    imageSize: Dp = 55.dp,
    flagWidth: Dp = 18.dp,
    flagHeight: Dp = 12.dp,
    nameFontSize: TextUnit = 12.sp,
    nameTrailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        horizontalArrangement = if (alignment == Alignment.Start) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.then(if (onClick != null) Modifier.appClickable { onClick() } else Modifier),
    ) {
        if (alignment == Alignment.End) {
            NameColumn(
                name = name ?: "",
                result = result,
                record = record,
                textAlign = TextAlign.End,
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(1f),
                nameFontSize = nameFontSize,
                nameTrailingContent = nameTrailingContent,
            )
            Spacer(modifier = Modifier.width(6.dp))
            FighterImage(
                imageUrl = imageUrl ?: "",
                name = name ?: "",
                countryCode = countryCode ?: "",
                result = result,
                alignment = alignment,
                imageSize = imageSize,
                flagWidth = flagWidth,
                flagHeight = flagHeight
            )
        } else {
            FighterImage(
                imageUrl = imageUrl ?: "",
                name = name ?: "",
                countryCode = countryCode ?: "",
                result = result,
                alignment = alignment,
                imageSize = imageSize,
                flagWidth = flagWidth,
                flagHeight = flagHeight
            )
            Spacer(modifier = Modifier.width(6.dp))
            NameColumn(
                name = name ?: "",
                result = result,
                record = record,
                textAlign = TextAlign.Start,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
                nameFontSize = nameFontSize,
                nameTrailingContent = nameTrailingContent,
            )
        }
    }
}
