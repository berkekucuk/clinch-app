package com.berkekucuk.mmaapp.core.presentation.colors

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val DarkColors = AppColors(
    isDark = true,
    topBarBackground = Color(0xFF1A1A1A),
    pagerBackground = Color.Black,
    dropdownMenuBackground = Color(0xFF2A2A2A),
    textPrimary = Color(0xFFECEDEF),
    textSecondary = Color(0xFF8B8E90),
    dateColor = Color(0xFFB0B0B0),
    ufcRed = Color(0xFFD20909),
    winnerFrame = Color(0xFF33A854),
    upcomingColor = Color(0xFF525252),
    upcomingColor2 = Color(0xFFE2E2E2),
    winColor = Color(0xFF47BE44),
    winColor2 = Color(0xFFBAE5B6),
    loseColor = Color(0xFFC6472A),
    loseColor2 = Color(0xFFEAB6AC),
    drawColor = Color(0xFF70ADFA),
    drawColor2 = Color(0xFFC5DEFD),
    noContestColor = Color(0xFFF9D03B),
    noContestColor2 = Color(0xFFFBECB6),
    signInButton = Color(0xFF4CAF50),
    white = Color.White,
    black = Color.Black,
    fightItemBackground = Color(0xFF171C1F),
    dividerColor = Color(0xFF2C2C2C),
    cardHeaderBackground = Color(0xFF20232A),
    cardBorder = Color(0xFF3E4149),
    fighterDetailTopBar = Brush.verticalGradient(
        colors = listOf(Color(0xFF111E2E), Color(0xFF0B141E), Color(0xFF070A11))
    ),
    eventDetailTopBar = Brush.verticalGradient(
        colors = listOf(Color(0xFF200000), Color(0xFF150000), Color(0xFF0A0000))
    ),
    eventsTopBar = Brush.verticalGradient(
        colors = listOf(Color(0xFF161B1F), Color(0xFF101418), Color(0xFF0A0C0F))
    ),
    rankingTopBar = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A1020), Color(0xFF14121C), Color(0xFF100E18))
    ),
    rankingChampionBadge = Brush.horizontalGradient(
        colors = listOf(Color(0xFFD4A843), Color(0xFFB8922E))
    ),
    rankingWeightClassAccent = Color(0xFF3A3A3A),
    radarGrid = Color(0xFF3A3A3A),
    radarLabel = Color(0xFF8B8E90),
    radarRed = Color(0xFFE53935),
    radarRedFill = Color(0x40E53935),
    radarBlue = Color(0xFF1E88E5),
    radarBlueFill = Color(0x401E88E5),
    cardShadowElevation = 0.dp,
)