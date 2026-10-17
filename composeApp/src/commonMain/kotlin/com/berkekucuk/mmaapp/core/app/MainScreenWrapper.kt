package com.berkekucuk.mmaapp.core.app

import com.berkekucuk.mmaapp.core.presentation.AppTheme
import com.berkekucuk.mmaapp.presentation.screens.home.HomeScreenRoot
import com.berkekucuk.mmaapp.presentation.screens.rankings.RankingsScreenRoot
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.berkekucuk.mmaapp.presentation.screens.menu.MenuScreenRoot
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition

@Composable
fun MainScreenWrapper(
    onNavigateToEventDetail: (String) -> Unit,
    onNavigateToRankingDetail: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToProfileEdit: () -> Unit,
    onNavigateToFighterSearch: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToSendNotification: () -> Unit,
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val navigationThrottle = remember { NavigationThrottle() }

    Scaffold(
        containerColor = AppTheme.colors.topBarBackground,
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
        bottomBar = {
            Column {
                HorizontalDivider(color = AppTheme.colors.dividerColor, thickness = 0.5.dp)
                NavigationBar(
                    containerColor = AppTheme.colors.topBarBackground,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any {
                            it.hasRoute(item.route::class)
                        } == true

                        val label = when (item.route) {
                            Route.Home -> AppTheme.strings.navEvents
                            Route.Rankings -> AppTheme.strings.navRankings
                            Route.Menu -> AppTheme.strings.navMenu
                            else -> item.name
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                bottomNavController.safeNavigate(
                                    throttle = navigationThrottle,
                                    route = item.route
                                ) {
                                    popUpTo(bottomNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = label
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AppTheme.colors.ufcRed,
                                selectedTextColor = AppTheme.colors.ufcRed,
                                indicatorColor = Color.Transparent,
                                unselectedIconColor = AppTheme.colors.textSecondary,
                                unselectedTextColor = AppTheme.colors.textSecondary
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = Route.Home,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            composable<Route.Home> {
                HomeScreenRoot(
                    onNavigateToEventDetail = onNavigateToEventDetail,
                    onNavigateToFighterSearch = onNavigateToFighterSearch,
                )
            }

            composable<Route.Rankings> {
                RankingsScreenRoot(
                    onNavigateToRankingDetail = onNavigateToRankingDetail
                )
            }

            composable<Route.Menu> {
                MenuScreenRoot(
                    onNavigateToProfile = onNavigateToProfile,
                    onNavigateToProfileEdit = onNavigateToProfileEdit,
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToLeaderboard = onNavigateToLeaderboard,
                    onNavigateToSendNotification = onNavigateToSendNotification,
                )
            }
        }
    }
}
