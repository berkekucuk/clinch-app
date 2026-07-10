package com.berkekucuk.mmaapp.core.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.channels.Channel
import com.berkekucuk.mmaapp.core.presentation.AppLanguage
import com.berkekucuk.mmaapp.core.presentation.colors.DarkColors
import com.berkekucuk.mmaapp.core.presentation.strings.EnStrings
import com.berkekucuk.mmaapp.core.presentation.colors.LightColors
import com.berkekucuk.mmaapp.core.presentation.colors.LocalAppColors
import com.berkekucuk.mmaapp.core.presentation.strings.LocalAppStrings
import com.berkekucuk.mmaapp.core.presentation.LocalMeasurementUnit
import com.berkekucuk.mmaapp.core.presentation.LocalOddsFormat
import com.berkekucuk.mmaapp.core.presentation.MeasurementUnit
import com.berkekucuk.mmaapp.core.presentation.OddsFormat
import com.berkekucuk.mmaapp.core.presentation.ThemeMode
import com.berkekucuk.mmaapp.core.presentation.LocalThemeMode
import com.berkekucuk.mmaapp.core.presentation.strings.TrStrings
import com.berkekucuk.mmaapp.core.storage.LanguageStorage
import com.berkekucuk.mmaapp.core.storage.MeasurementUnitStorage
import com.berkekucuk.mmaapp.core.storage.OddsFormatStorage
import com.berkekucuk.mmaapp.core.storage.ThemeStorage
import com.berkekucuk.mmaapp.core.utils.SetStatusBarAppearance
import org.koin.compose.koinInject
import com.berkekucuk.mmaapp.presentation.screens.event_detail.EventDetailScreenRoot
import com.berkekucuk.mmaapp.presentation.screens.fight_detail.FightDetailScreenRoot
import com.berkekucuk.mmaapp.presentation.screens.fighter_detail.FighterDetailScreenRoot
import com.berkekucuk.mmaapp.presentation.screens.fighter_search.FighterSearchScreenRoot
import com.berkekucuk.mmaapp.presentation.screens.interaction_list.InteractionListScreenRoot
import com.berkekucuk.mmaapp.presentation.screens.profile.ProfileScreenRoot
import com.berkekucuk.mmaapp.presentation.screens.settings.SettingsScreenRoot
import com.berkekucuk.mmaapp.presentation.screens.profile_edit.ProfileEditScreenRoot
import com.berkekucuk.mmaapp.presentation.screens.ranking_detail.RankingDetailScreenRoot
import com.berkekucuk.mmaapp.presentation.screens.leaderboard.LeaderboardScreenRoot
import com.berkekucuk.mmaapp.presentation.screens.blocked_users.BlockedUsersScreenRoot
import com.berkekucuk.mmaapp.presentation.screens.user_search.UserSearchScreenRoot

object DeepLinkManager {
    private val _route = Channel<Route>(Channel.BUFFERED)
    val route = _route.receiveAsFlow()

    fun navigateToFight(fightId: String) {
        _route.trySend(Route.FightDetail(fightId))
    }

    fun navigateToEvent(eventId: String) {
        _route.trySend(Route.EventDetail(eventId))
    }
}

@Composable
fun App() {
    val rootNavController = rememberNavController()

    val languageStorage: LanguageStorage = koinInject()
    val languageState = remember {
        mutableStateOf(
            try {
                AppLanguage.valueOf(languageStorage.load())
            } catch (_: Exception) {
                AppLanguage.EN
            }
        )
    }
    val language by languageState
    val strings = if (language == AppLanguage.EN) EnStrings else TrStrings

    val themeStorage: ThemeStorage = koinInject()
    val themeModeState = remember {
        mutableStateOf(
            try {
                ThemeMode.valueOf(themeStorage.load())
            }
            catch (_: Exception) {
                ThemeMode.DARK
            }
        )
    }
    val themeMode by themeModeState
    val colors = when(themeMode) {
        ThemeMode.DARK -> DarkColors
        ThemeMode.LIGHT -> LightColors
    }
    SetStatusBarAppearance(isDarkTheme = colors.isDark)

    val measurementUnitStorage: MeasurementUnitStorage = koinInject()
    val measurementUnitState = remember {
        mutableStateOf(
            try {
                MeasurementUnit.valueOf(measurementUnitStorage.load())
            } catch (_: Exception) {
                MeasurementUnit.METRIC
            }
        )
    }
    val measurementUnit by measurementUnitState

    val oddsFormatStorage: OddsFormatStorage = koinInject()
    val oddsFormatState = remember {
        mutableStateOf(
            try {
                OddsFormat.valueOf(oddsFormatStorage.load())
            } catch (_: Exception) {
                OddsFormat.DECIMAL
            }
        )
    }
    val oddsFormat by oddsFormatState

    LaunchedEffect(Unit) {
        DeepLinkManager.route.collect { route ->
            rootNavController.navigate(route)
        }
    }

    CompositionLocalProvider(
        LocalAppStrings provides strings,
        LocalMeasurementUnit provides measurementUnit,
        LocalOddsFormat provides oddsFormat,
        LocalAppColors provides colors,
        LocalThemeMode provides themeMode
    ) {
        NavHost(
            navController = rootNavController,
            startDestination = Route.MainGraph,
            modifier = Modifier
                .fillMaxSize()
                .background(colors.pagerBackground)
        ) {
            composable<Route.MainGraph>(
                enterTransition = NavTransitions.slideFromLeft,
                exitTransition = NavTransitions.slideOutToLeft,
                popEnterTransition = NavTransitions.slideFromLeft,
                popExitTransition = NavTransitions.slideOutToLeft
            ) {
                MainScreenWrapper(
                    onNavigateToEventDetail = { eventId ->
                        rootNavController.navigate(Route.EventDetail(eventId))
                    },
                    onNavigateToRankingDetail = { weightClassId ->
                        rootNavController.navigate(Route.RankingDetail(weightClassId))
                    },
                    onNavigateToProfile = { userId ->
                        rootNavController.navigate(Route.Profile(userId))
                    },
                    onNavigateToProfileEdit = {
                        rootNavController.navigate(Route.ProfileEdit)
                    },
                    onNavigateToFighterSearch = {
                        rootNavController.navigate(Route.FighterSearch())
                    },
                    onNavigateToSettings = {
                        rootNavController.navigate(Route.Settings)
                    },
                    onNavigateToLeaderboard = {
                        rootNavController.navigate(Route.Leaderboard)
                    },
                )
            }

            composable<Route.EventDetail>(
                enterTransition = NavTransitions.slideFromRight,
                exitTransition = NavTransitions.slideOutToLeft,
                popEnterTransition = NavTransitions.slideFromLeft,
                popExitTransition = NavTransitions.slideOutToRight
            ) {
                EventDetailScreenRoot(
                    onNavigateToFightDetail = { fightId ->
                        rootNavController.navigate(
                            Route.FightDetail(
                                fightId = fightId,
                                fromEventDetail = true
                            )
                        )
                    },
                    onNavigateBack = { rootNavController.navigateUp() }
                )
            }

            composable<Route.FightDetail>(
                enterTransition = NavTransitions.slideFromRight,
                exitTransition = NavTransitions.slideOutToLeft,
                popEnterTransition = NavTransitions.slideFromLeft,
                popExitTransition = NavTransitions.slideOutToRight
            ) {
                FightDetailScreenRoot(
                    onNavigateToFighterDetail = { fighterId ->
                        rootNavController.navigate(Route.FighterDetail(fighterId))
                    },
                    onNavigateToEventDetail = { eventId ->
                        rootNavController.navigate(Route.EventDetail(eventId, fromFightDetail = true))
                    },
                    onNavigateToLeaderboard = {
                        rootNavController.navigate(Route.Leaderboard)
                    },
                    onNavigateBack = { rootNavController.navigateUp() }
                )
            }

            composable<Route.FighterDetail>(
                enterTransition = NavTransitions.slideFromRight,
                exitTransition = NavTransitions.slideOutToLeft,
                popEnterTransition = NavTransitions.slideFromLeft,
                popExitTransition = NavTransitions.slideOutToRight
            ) {
                FighterDetailScreenRoot(
                    onNavigateToFightDetail = { fightId, fighterId ->
                        rootNavController.navigate(
                            Route.FightDetail(
                                fightId = fightId,
                                fighterId = fighterId,
                                fromEventDetail = false
                            )
                        )
                    },
                    onNavigateBack = { rootNavController.navigateUp() }
                )
            }

            composable<Route.ProfileEdit>(
                enterTransition = NavTransitions.slideFromRight,
                exitTransition = NavTransitions.slideOutToLeft,
                popEnterTransition = NavTransitions.slideFromLeft,
                popExitTransition = NavTransitions.slideOutToRight
            ) {
                ProfileEditScreenRoot(
                    onNavigateBack = { rootNavController.navigateUp() }
                )
            }

            composable<Route.Profile>(
                enterTransition = NavTransitions.slideFromRight,
                exitTransition = NavTransitions.slideOutToLeft,
                popEnterTransition = NavTransitions.slideFromLeft,
                popExitTransition = NavTransitions.slideOutToRight
            ) {
                ProfileScreenRoot(
                    onNavigateBack = { rootNavController.navigateUp() },
                    onNavigateToInteractionList = { userId, type ->
                        rootNavController.navigate(Route.InteractionList(userId, type))
                    },
                    onNavigateToFightDetail = { fightId ->
                        rootNavController.navigate(Route.FightDetail(fightId = fightId))
                    }
                )
            }

            composable<Route.InteractionList>(
                enterTransition = NavTransitions.slideFromRight,
                exitTransition = NavTransitions.slideOutToLeft,
                popEnterTransition = NavTransitions.slideFromLeft,
                popExitTransition = NavTransitions.slideOutToRight
            ) {
                InteractionListScreenRoot(
                    onNavigateBack = { rootNavController.navigateUp() },
                    onNavigateToFighterDetail = { fighterId ->
                        rootNavController.navigate(Route.FighterDetail(fighterId))
                    },
                    onNavigateToFighterSearch = { interactionType ->
                        rootNavController.navigate(Route.FighterSearch(interactionType))
                    }
                )
            }

            composable<Route.RankingDetail>(
                enterTransition = NavTransitions.slideFromRight,
                exitTransition = NavTransitions.slideOutToLeft,
                popEnterTransition = NavTransitions.slideFromLeft,
                popExitTransition = NavTransitions.slideOutToRight
            ) {
                RankingDetailScreenRoot(
                    onNavigateBack = { rootNavController.navigateUp() },
                    onNavigateToFighterDetail = { fighterId ->
                        rootNavController.navigate(Route.FighterDetail(fighterId))
                    }
                )
            }

            composable<Route.FighterSearch>(
                enterTransition = NavTransitions.slideFromRight,
                exitTransition = NavTransitions.slideOutToLeft,
                popEnterTransition = NavTransitions.slideFromLeft,
                popExitTransition = NavTransitions.slideOutToRight
            ) {
                FighterSearchScreenRoot(
                    onNavigateToFighterDetail = { fighterId ->
                        rootNavController.navigate(Route.FighterDetail(fighterId))
                    },
                    onNavigateBack = { rootNavController.navigateUp() }
                )
            }

            composable<Route.Settings>(
                enterTransition = NavTransitions.slideFromRight,
                exitTransition = NavTransitions.slideOutToLeft,
                popEnterTransition = NavTransitions.slideFromLeft,
                popExitTransition = NavTransitions.slideOutToRight
            ) {
                SettingsScreenRoot(
                    onBackClick = { rootNavController.navigateUp() },
                    onLanguageChange = {
                        languageState.value = it
                        languageStorage.save(it.name)
                    },
                    onMeasurementUnitChange = {
                        measurementUnitState.value = it
                        measurementUnitStorage.save(it.name)
                    },
                    onOddsFormatChange = {
                        oddsFormatState.value = it
                        oddsFormatStorage.save(it.name)
                    },
                    onThemeModeChange = {
                        themeModeState.value = it
                        themeStorage.save(it.name)
                    },
                    onBlockedUsersClick = {
                        rootNavController.navigate(Route.BlockedUsers)
                    }
                )
            }

            composable<Route.Leaderboard>(
                enterTransition = NavTransitions.slideFromRight,
                exitTransition = NavTransitions.slideOutToLeft,
                popEnterTransition = NavTransitions.slideFromLeft,
                popExitTransition = NavTransitions.slideOutToRight
            ) {
                LeaderboardScreenRoot(
                    onNavigateBack = { rootNavController.navigateUp() },
                    onNavigateToProfile = { userId ->
                        rootNavController.navigate(Route.Profile(userId))
                    },
                    onNavigateToUserSearch = {
                        rootNavController.navigate(Route.UserSearch)
                    }
                )
            }

            composable<Route.BlockedUsers>(
                enterTransition = NavTransitions.slideFromRight,
                exitTransition = NavTransitions.slideOutToLeft,
                popEnterTransition = NavTransitions.slideFromLeft,
                popExitTransition = NavTransitions.slideOutToRight
            ) {
                BlockedUsersScreenRoot(
                    onNavigateBack = { rootNavController.navigateUp() },
                    onNavigateToProfile = { userId ->
                        rootNavController.navigate(Route.Profile(userId))
                    }
                )
            }

            composable<Route.UserSearch>(
                enterTransition = NavTransitions.slideFromRight,
                exitTransition = NavTransitions.slideOutToLeft,
                popEnterTransition = NavTransitions.slideFromLeft,
                popExitTransition = NavTransitions.slideOutToRight
            ) {
                UserSearchScreenRoot(
                    onNavigateBack = { rootNavController.navigateUp() },
                    onNavigateToUserProfile = { userId ->
                        rootNavController.navigate(Route.Profile(userId))
                    }
                )
            }
        }
    }
}
