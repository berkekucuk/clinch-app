package com.berkekucuk.mmaapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
import com.berkekucuk.mmaapp.domain.model.Fight

@Composable
fun FightItem(
    fight: Fight,
    modifier: Modifier = Modifier,
    backgroundColor: Color = LocalAppColors.current.fightItemBackground,
    onClick: (() -> Unit)? = null,
    onRedCornerClick: (() -> Unit)? = null,
    onBlueCornerClick: (() -> Unit)? = null,
) {
    val redCorner = fight.redCorner
    val blueCorner = fight.blueCorner

    val methodText = remember(fight.methodType, fight.methodDetail) {
        buildString {
            if (!fight.methodType.isNullOrBlank()) {
                append(fight.methodType)
                if (!fight.methodDetail.isNullOrBlank()) {
                    append(" - ${fight.methodDetail}")
                }
            }
        }
    }

    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current
    val isOpenWeight = fight.weightClassId.equals("OW", ignoreCase = true)
    val weightClassLabel = if (isOpenWeight) "" else strings.weightClassDisplayName(fight.weightClassId)

    val isTitleFight = !fight.titleType.isNullOrBlank()

    val titleLabel = remember(fight.titleType, weightClassLabel) {
        val titleType = fight.titleType?.trim()
        if (titleType.isNullOrBlank()) null
        else {
            when (titleType.lowercase()) {
                "undisputed" -> strings.titleFightUndisputed(weightClassLabel)
                "interim" -> strings.titleFightInterim(weightClassLabel)
                else -> if (weightClassLabel.isNotBlank()) "$weightClassLabel $titleType 🏆" else "$titleType 🏆"
            }
        }
    }

    val titleFightHeaderText = remember(titleLabel, methodText, colors.championGold, colors.textSecondary) {
        if (titleLabel.isNullOrBlank()) null
        else {
            buildAnnotatedString {
                withStyle(SpanStyle(color = colors.championGold, fontWeight = FontWeight.SemiBold)) {
                    append(titleLabel)
                }
                if (methodText.isNotBlank()) {
                    withStyle(SpanStyle(color = colors.textSecondary, fontWeight = FontWeight.Medium)) {
                        append(" • $methodText")
                    }
                }
            }
        }
    }

    val redRecord = remember(redCorner) {
        redCorner?.recordAfterFight?.toString() ?: redCorner?.fighter?.record?.toString()
    }

    val blueRecord = remember(blueCorner) {
        blueCorner?.recordAfterFight?.toString() ?: blueCorner?.fighter?.record?.toString()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .then(if (onClick != null) Modifier.appClickable { onClick() } else Modifier)
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(0.3f))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isTitleFight && titleFightHeaderText != null) {
                    Text(
                        text = titleFightHeaderText,
                        fontSize = 12.sp,
                        maxLines = 1,
                    )
                } else {
                    val headerText = methodText.ifEmpty { weightClassLabel }
                    Text(
                        text = headerText,
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FighterPortrait(
                    name = redCorner?.fighter?.name,
                    imageUrl = redCorner?.fighter?.imageUrl,
                    countryCode = redCorner?.fighter?.countryCode,
                    result = redCorner?.result?.name,
                    record = redRecord,
                    alignment = Alignment.Start,
                    modifier = Modifier.weight(1f),
                    onClick = onRedCornerClick,
                )

                Box(
                    modifier = Modifier.height(55.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "VS",
                        color = colors.textSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                FighterPortrait(
                    name = blueCorner?.fighter?.name,
                    imageUrl = blueCorner?.fighter?.imageUrl,
                    countryCode = blueCorner?.fighter?.countryCode,
                    result = blueCorner?.result?.name,
                    record = blueRecord,
                    alignment = Alignment.End,
                    modifier = Modifier.weight(1f),
                    onClick = onBlueCornerClick,
                )
            }

            Spacer(modifier = Modifier.weight(0.4f))
        }
    }
}
