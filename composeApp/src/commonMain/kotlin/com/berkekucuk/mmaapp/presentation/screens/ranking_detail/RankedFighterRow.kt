package com.berkekucuk.mmaapp.presentation.screens.ranking_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
import com.berkekucuk.mmaapp.presentation.components.FighterPortrait

@Composable
fun RankedFighterRow(
    rankNumber: Int,
    isChampion: Boolean = false,
    name: String,
    record: String,
    imageUrl: String,
    countryCode: String? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onFighterClicked: () -> Unit
) {
    val colors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val rankLabel = if (isChampion) strings.rankingsChampionRankLabel else rankNumber.toString()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFighterClicked() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(32.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isChampion) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(colors.rankingChampionBadge),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rankLabel,
                        color = colors.pagerBackground,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            } else {
                Text(
                    text = rankLabel,
                    color = colors.textSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }

        Spacer(Modifier.width(10.dp))

        FighterPortrait(
            name = name,
            imageUrl = imageUrl,
            countryCode = countryCode,
            result = null,
            record = record,
            alignment = Alignment.Start,
            modifier = Modifier.weight(1f),
            imageSize = 42.dp,
            flagWidth = 14.dp,
            flagHeight = 9.dp,
            nameFontSize = 14.sp,
        )

        if (trailingContent != null) {
            Spacer(Modifier.width(8.dp))
            trailingContent()
        }
    }
}
