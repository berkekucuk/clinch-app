package com.berkekucuk.mmaapp.core.presentation.colors

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

val LightColors = AppColors(
    isDark = false,
    topBarBackground = Color(0xFFFFFFFF),
    pagerBackground = Color(0xFFEAEAEA),
    dropdownMenuBackground = Color(0xFFF5F5F5),
    textPrimary = Color(0xFF1C1917),
    textSecondary = Color(0xFF78716C),
    dateColor = Color(0xFF4B5563),
    ufcRed = Color(0xFFD20909),
    winnerFrame = Color(0xFF2E8B42),
    upcomingColor = Color(0xFF9E9E9E),
    upcomingColor2 = Color(0xFF616161),
    winColor = Color(0xFF47BE44),
    winColor2 = Color(0xFF1B5E20),
    loseColor = Color(0xFFC6472A),
    loseColor2 = Color(0xFF8E2211),
    drawColor = Color(0xFF70ADFA),
    drawColor2 = Color(0xFF1976D2),
    noContestColor = Color(0xFFF9D03B),
    noContestColor2 = Color(0xFFF57F17),
    signInButton = Color(0xFF4CAF50),
    white = Color.White,
    black = Color.Black,
    fightItemBackground = Color(0xFFFFFFFF),
    dividerColor = Color(0xFFE5E0DA),
    cardHeaderBackground = Color(0xFFF5F5F5),
    cardBorder = Color(0xFFE8E3DD),
    fighterDetailTopBar = SolidColor(Color.White),
    eventDetailTopBar = SolidColor(Color.White),
    eventsTopBar = SolidColor(Color.White),
    rankingTopBar = SolidColor(Color.White),
    rankingChampionBadge = Brush.horizontalGradient(
        colors = listOf(Color(0xFFD4A843), Color(0xFFB8922E))
    ),
    rankingWeightClassAccent = Color(0xFFD6D3CD),
    radarGrid = Color(0xFFD6D3CD),
    radarLabel = Color(0xFF78716C),
    radarRed = Color(0xFFE53935),
    radarRedFill = Color(0x30E53935),
    radarBlue = Color(0xFF1E88E5),
    radarBlueFill = Color(0x301E88E5),
    cardShadowElevation = 6.dp,
)