package com.berkekucuk.mmaapp.core.app

import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.ui.graphics.vector.ImageVector

data class TopLevelRoute<T : Route>(
    val name: String,
    val route: T,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    TopLevelRoute("Events", Route.Home, Icons.Filled.SportsMma),
    TopLevelRoute("News", Route.News, Icons.Filled.Newspaper),
    TopLevelRoute("Rankings", Route.Rankings, Icons.Filled.Leaderboard),
    TopLevelRoute("Leaderboard", Route.Leaderboard, Icons.Filled.EmojiEvents),
    TopLevelRoute("Menu", Route.Menu, Icons.Filled.Menu)
)