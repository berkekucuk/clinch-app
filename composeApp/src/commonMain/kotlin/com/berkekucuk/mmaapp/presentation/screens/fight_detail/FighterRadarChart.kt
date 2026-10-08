package com.berkekucuk.mmaapp.presentation.screens.fight_detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
import com.berkekucuk.mmaapp.domain.model.Fighter
import com.berkekucuk.mmaapp.domain.model.Participant
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private data class RadarData(
    val values: List<Float>,
    val color: Color,
    val fillColor: Color,
)

@Composable
fun FighterRadarChart(
    modifier: Modifier = Modifier,
    fighter: Fighter? = null,
    redCorner: Participant? = null,
    blueCorner: Participant? = null,
) {
    val strings = LocalAppStrings.current
    val colors = LocalAppColors.current
    val textMeasurer = rememberTextMeasurer()

    val axisLabels = remember(strings) {
        listOf(
            strings.radarLabelSlpm,
            strings.radarLabelStrAcc,
            strings.radarLabelStrDef,
            strings.radarLabelTdAvg,
            strings.radarLabelTdAcc,
            strings.radarLabelTdDef,
            strings.radarLabelSubRate,
            strings.radarLabelKoTkoRate,
            strings.radarLabelWinRate,
        )
    }

    val isSingleFighter = fighter != null

    val axisValues = remember(fighter) {
        if (fighter != null) {
            listOf(
                formatRadarRate(fighter.slpm),
                formatRadarPercent(fighter.strAcc),
                formatRadarPercent(fighter.strDef),
                formatRadarRate(fighter.tdAvg),
                formatRadarPercent(fighter.tdAcc),
                formatRadarPercent(fighter.tdDef),
                formatRadarPercent(fighter.submissionRate),
                formatRadarPercent(fighter.koTkoRate),
                formatRadarPercent(fighter.winRate),
            )
        } else {
            emptyList()
        }
    }

    val singleFighterData = remember(fighter, colors) {
        if (fighter != null) {
            RadarData(
                values = buildRadarValues(fighter),
                color = colors.statSingleBar,
                fillColor = colors.statSingleBar.copy(alpha = 0.28f),
            )
        } else {
            null
        }
    }

    val redData = remember(redCorner, colors) {
        if (!isSingleFighter) {
            RadarData(
                values = buildRadarValues(redCorner?.fighter),
                color = colors.radarRed,
                fillColor = colors.radarRedFill,
            )
        } else {
            null
        }
    }

    val blueData = remember(blueCorner, colors) {
        if (!isSingleFighter) {
            RadarData(
                values = buildRadarValues(blueCorner?.fighter),
                color = colors.radarBlue,
                fillColor = colors.radarBlueFill,
            )
        } else {
            null
        }
    }

    val chartAspectRatio = if (isSingleFighter) 1.15f else 1f
    val radiusRatio = if (isSingleFighter) 0.31f else 0.33f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.fightItemBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(chartAspectRatio),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().aspectRatio(chartAspectRatio)) {
                val centerX = size.width / 2f
                val centerY = size.height / 2f
                val radius = size.minDimension * radiusRatio

                for (level in 1..4) {
                    val r = radius * level / 4f
                    val gridPath = Path()
                    for (i in 0 until RADAR_AXIS_COUNT) {
                        val angle = (2 * PI / RADAR_AXIS_COUNT) * i - PI / 2
                        val x = centerX + r * cos(angle).toFloat()
                        val y = centerY + r * sin(angle).toFloat()
                        if (i == 0) gridPath.moveTo(x, y) else gridPath.lineTo(x, y)
                    }
                    gridPath.close()
                    drawPath(gridPath, colors.radarGrid, style = Stroke(width = 1f))
                }

                for (i in 0 until RADAR_AXIS_COUNT) {
                    val angle = (2 * PI / RADAR_AXIS_COUNT) * i - PI / 2
                    val x = centerX + radius * cos(angle).toFloat()
                    val y = centerY + radius * sin(angle).toFloat()
                    drawLine(colors.radarGrid, Offset(centerX, centerY), Offset(x, y), strokeWidth = 1f)
                }

                if (isSingleFighter && singleFighterData != null) {
                    drawRadarPolygon(
                        drawScope = this,
                        centerX = centerX,
                        centerY = centerY,
                        radius = radius,
                        data = singleFighterData,
                        drawDots = true,
                        dotBackgroundColor = colors.fightItemBackground,
                    )
                    drawRadarLabelsWithValues(
                        drawScope = this,
                        textMeasurer = textMeasurer,
                        centerX = centerX,
                        centerY = centerY,
                        radius = radius,
                        labels = axisLabels,
                        values = axisValues,
                        labelColor = colors.radarLabel,
                        valueColor = colors.textPrimary,
                    )
                } else {
                    if (redData != null) drawRadarPolygon(this, centerX, centerY, radius, redData)
                    if (blueData != null) drawRadarPolygon(this, centerX, centerY, radius, blueData)
                    drawRadarLabels(this, textMeasurer, centerX, centerY, radius, axisLabels, colors.radarLabel)
                }
            }
        }

        if (!isSingleFighter) {
            RadarCornerRow(
                redName = redCorner?.fighter?.name ?: "Red",
                blueName = blueCorner?.fighter?.name ?: "Blue",
            )
        }
    }
}

@Composable
private fun RadarCornerRow(redName: String, blueName: String) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadarCornerItem(color = colors.radarRed, name = redName)
        RadarCornerItem(color = colors.radarBlue, name = blueName)
    }
}

@Composable
private fun RadarCornerItem(color: Color, name: String) {
    val colors = LocalAppColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Canvas(modifier = Modifier.size(10.dp)) {
            drawCircle(color, radius = 5.dp.toPx())
        }
        Text(
            text = name,
            color = colors.textSecondary,
            fontSize = 11.sp,
        )
    }
}

private fun drawRadarPolygon(
    drawScope: DrawScope,
    centerX: Float,
    centerY: Float,
    radius: Float,
    data: RadarData,
    drawDots: Boolean = false,
    dotBackgroundColor: Color = Color.Transparent,
) {
    val path = Path()
    val points = mutableListOf<Offset>()
    data.values.forEachIndexed { i, value ->
        val angle = (2 * PI / RADAR_AXIS_COUNT) * i - PI / 2
        val r = radius * value.coerceIn(0.04f, 1f)
        val x = centerX + r * cos(angle).toFloat()
        val y = centerY + r * sin(angle).toFloat()
        val pt = Offset(x, y)
        points.add(pt)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawScope.drawPath(path, data.fillColor, style = Fill)
    drawScope.drawPath(path, data.color, style = Stroke(width = 2f))

    if (drawDots) {
        points.forEach { pt ->
            drawScope.drawCircle(data.color, radius = 4f, center = pt)
            if (dotBackgroundColor != Color.Transparent) {
                drawScope.drawCircle(dotBackgroundColor, radius = 2f, center = pt)
            }
        }
    }
}

private fun drawRadarLabels(
    drawScope: DrawScope,
    textMeasurer: TextMeasurer,
    centerX: Float,
    centerY: Float,
    radius: Float,
    labels: List<String>,
    labelColor: Color,
) {
    val labelRadius = radius * 1.18f
    val style = TextStyle(
        color = labelColor,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
    )
    labels.forEachIndexed { i, label ->
        val angle = (2 * PI / RADAR_AXIS_COUNT) * i - PI / 2
        val x = centerX + labelRadius * cos(angle).toFloat()
        val y = centerY + labelRadius * sin(angle).toFloat()
        val textLayout = textMeasurer.measure(label, style)
        drawScope.drawText(
            textLayoutResult = textLayout,
            topLeft = Offset(
                x = x - textLayout.size.width / 2f,
                y = y - textLayout.size.height / 2f,
            ),
        )
    }
}

private fun drawRadarLabelsWithValues(
    drawScope: DrawScope,
    textMeasurer: TextMeasurer,
    centerX: Float,
    centerY: Float,
    radius: Float,
    labels: List<String>,
    values: List<String>,
    labelColor: Color,
    valueColor: Color,
) {
    val labelRadius = radius * 1.20f
    val labelStyle = TextStyle(
        color = labelColor,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
    )
    val valueStyle = TextStyle(
        color = valueColor,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
    )

    labels.forEachIndexed { i, label ->
        val value = values.getOrNull(i) ?: ""
        val angle = (2 * PI / RADAR_AXIS_COUNT) * i - PI / 2
        val targetX = centerX + labelRadius * cos(angle).toFloat()
        val targetY = centerY + labelRadius * sin(angle).toFloat()

        val labelLayout = textMeasurer.measure(label, labelStyle)
        val valueLayout = textMeasurer.measure(value, valueStyle)

        val totalWidth = maxOf(labelLayout.size.width, valueLayout.size.width).toFloat()
        val totalHeight = (labelLayout.size.height + valueLayout.size.height + 2).toFloat()

        val cosA = cos(angle).toFloat()
        val sinA = sin(angle).toFloat()

        val xOffset = when {
            cosA > 0.35f -> 0f
            cosA < -0.35f -> -totalWidth
            else -> -totalWidth / 2f
        }
        val yOffset = when {
            sinA > 0.35f -> 0f
            sinA < -0.35f -> -totalHeight
            else -> -totalHeight / 2f
        }

        val blockX = targetX + xOffset
        val blockY = targetY + yOffset

        drawScope.drawText(
            textLayoutResult = labelLayout,
            topLeft = Offset(
                x = blockX + (totalWidth - labelLayout.size.width) / 2f,
                y = blockY
            )
        )
        drawScope.drawText(
            textLayoutResult = valueLayout,
            topLeft = Offset(
                x = blockX + (totalWidth - valueLayout.size.width) / 2f,
                y = blockY + labelLayout.size.height + 2
            )
        )
    }
}