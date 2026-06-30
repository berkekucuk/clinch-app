package com.berkekucuk.mmaapp.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module
import com.berkekucuk.mmaapp.BuildConfig
import com.berkekucuk.mmaapp.core.utils.DateTimeProvider
import com.berkekucuk.mmaapp.core.utils.RateLimiter
import com.berkekucuk.mmaapp.core.utils.SystemDateTimeProvider
import com.berkekucuk.mmaapp.data.local.AppDatabase
import com.berkekucuk.mmaapp.data.local.getRoomDatabase
import com.berkekucuk.mmaapp.data.remote.factory.SupabaseClientFactory
import com.berkekucuk.mmaapp.data.remote.datasource.DeviceTokenRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.supabase.DeviceTokenSupabaseAPI
import com.berkekucuk.mmaapp.data.remote.datasource.EventRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.supabase.EventSupabaseAPI
import com.berkekucuk.mmaapp.data.remote.datasource.FightRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.supabase.FightSupabaseAPI
import com.berkekucuk.mmaapp.data.remote.datasource.FighterRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.supabase.FighterSupabaseAPI
import com.berkekucuk.mmaapp.data.remote.datasource.InteractionRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.supabase.InteractionSupabaseAPI
import com.berkekucuk.mmaapp.data.remote.datasource.WeightClassRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.supabase.WeightClassSupabaseAPI
import com.berkekucuk.mmaapp.data.remote.datasource.UserRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.supabase.UserSupabaseAPI
import com.berkekucuk.mmaapp.data.remote.datasource.LeaderboardRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.supabase.LeaderboardSupabaseAPI
import com.berkekucuk.mmaapp.data.repository.EventRepositoryImpl
import com.berkekucuk.mmaapp.data.repository.FighterRepositoryImpl
import com.berkekucuk.mmaapp.data.repository.WeightClassRepositoryImpl
import com.berkekucuk.mmaapp.data.repository.AuthRepositoryImpl
import com.berkekucuk.mmaapp.data.repository.UserRepositoryImpl
import com.berkekucuk.mmaapp.data.repository.NotificationRepositoryImpl
import com.berkekucuk.mmaapp.data.repository.PredictionRepositoryImpl
import com.berkekucuk.mmaapp.domain.repository.AuthRepository
import com.berkekucuk.mmaapp.domain.repository.NotificationRepository
import com.berkekucuk.mmaapp.domain.repository.PredictionRepository
import com.berkekucuk.mmaapp.data.remote.datasource.NotificationRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.supabase.NotificationSupabaseAPI
import com.berkekucuk.mmaapp.data.remote.datasource.PredictionRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.supabase.PredictionSupabaseAPI
import com.berkekucuk.mmaapp.data.repository.FightRepositoryImpl
import com.berkekucuk.mmaapp.data.repository.InteractionRepositoryImpl
import com.berkekucuk.mmaapp.domain.repository.EventRepository
import com.berkekucuk.mmaapp.domain.repository.FightRepository
import com.berkekucuk.mmaapp.domain.repository.FighterRepository
import com.berkekucuk.mmaapp.domain.repository.InteractionRepository
import com.berkekucuk.mmaapp.domain.repository.WeightClassRepository
import com.berkekucuk.mmaapp.domain.repository.UserRepository
import com.berkekucuk.mmaapp.domain.repository.LeaderboardRepository
import com.berkekucuk.mmaapp.data.repository.LeaderboardRepositoryImpl
import com.berkekucuk.mmaapp.presentation.screens.blocked_users.BlockedUsersViewModel
import com.berkekucuk.mmaapp.presentation.screens.event_detail.EventDetailViewModel
import com.berkekucuk.mmaapp.presentation.screens.fighter_detail.FighterDetailViewModel
import com.berkekucuk.mmaapp.presentation.screens.fight_detail.FightDetailViewModel
import com.berkekucuk.mmaapp.presentation.screens.menu.MenuViewModel
import com.berkekucuk.mmaapp.presentation.screens.interaction_list.InteractionListViewModel
import com.berkekucuk.mmaapp.presentation.screens.profile.ProfileViewModel
import com.berkekucuk.mmaapp.presentation.screens.home.HomeViewModel
import com.berkekucuk.mmaapp.presentation.screens.profile_edit.ProfileEditViewModel
import com.berkekucuk.mmaapp.presentation.screens.ranking_detail.RankingDetailViewModel
import com.berkekucuk.mmaapp.presentation.screens.fighter_search.FighterSearchViewModel
import com.berkekucuk.mmaapp.presentation.screens.rankings.RankingViewModel
import com.berkekucuk.mmaapp.data.remote.datasource.AppConfigRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.supabase.AppConfigSupabaseAPI
import com.berkekucuk.mmaapp.domain.repository.AppConfigRepository
import com.berkekucuk.mmaapp.data.repository.AppConfigRepositoryImpl
import com.berkekucuk.mmaapp.data.remote.datasource.AppVersionRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.supabase.AppVersionSupabaseAPI
import com.berkekucuk.mmaapp.data.repository.AppVersionRepositoryImpl
import com.berkekucuk.mmaapp.domain.repository.AppVersionRepository
import com.berkekucuk.mmaapp.presentation.screens.leaderboard.LeaderboardViewModel
import com.berkekucuk.mmaapp.presentation.screens.settings.SettingsViewModel
import com.berkekucuk.mmaapp.presentation.screens.user_search.UserSearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named

val appModule = module {

    includes(platformModule)

    // application scope
    single(named("applicationScope")) {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    // time provider
    single<DateTimeProvider> { SystemDateTimeProvider() }

    // rate limiter
    single {
        RateLimiter(
            dateTimeProvider = get(),
            timeoutMs = 10_000L
        )
    }

    // supabase client
    single {
        SupabaseClientFactory.create(
            url = BuildConfig.SUPABASE_URL,
            key = BuildConfig.SUPABASE_KEY,
            googleClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
        )
    }

    // local db
    single<AppDatabase> {
        getRoomDatabase(get())
    }

    single {
        get<AppDatabase>().eventDao()
    }

    single {
        get<AppDatabase>().rankingDao()
    }

    single {
        get<AppDatabase>().fighterDao()
    }

    single {
        get<AppDatabase>().userDao()
    }

    single {
        get<AppDatabase>().notificationDao()
    }

    single {
        get<AppDatabase>().predictionDao()
    }

    single {
        get<AppDatabase>().fightDao()
    }

    single {
        get<AppDatabase>().interactionDao()
    }

    single {
        get<AppDatabase>().appConfigDao()
    }

    single {
        get<AppDatabase>().weeklyLeaderboardDao()
    }

    // remote data source
    single<EventRemoteDataSource> {
        EventSupabaseAPI(client = get())
    }

    single<WeightClassRemoteDataSource> {
        WeightClassSupabaseAPI(client = get())
    }

    single<FighterRemoteDataSource>{
        FighterSupabaseAPI(get())
    }

    single<UserRemoteDataSource> {
        UserSupabaseAPI(client = get(), dateTimeProvider = get())
    }

    single<LeaderboardRemoteDataSource> {
        LeaderboardSupabaseAPI(client = get())
    }

    single<DeviceTokenRemoteDataSource> {
        DeviceTokenSupabaseAPI(client = get())
    }

    single<NotificationRemoteDataSource> {
        NotificationSupabaseAPI(client = get())
    }

    single<PredictionRemoteDataSource> {
        PredictionSupabaseAPI(client = get())
    }

    single<FightRemoteDataSource> {
        FightSupabaseAPI(client = get())
    }

    single<InteractionRemoteDataSource> {
        InteractionSupabaseAPI(client = get())
    }

    single<AppConfigRemoteDataSource> {
        AppConfigSupabaseAPI(client = get())
    }

    single<AppVersionRemoteDataSource> {
        AppVersionSupabaseAPI(client = get())
    }

    single<AppVersionRepository> {
        AppVersionRepositoryImpl(
            remoteDataSource = get(),
            appVersionStorage = get()
        )
    }

    // repository
    single<FightRepository> {
        FightRepositoryImpl(
            fightDao = get(),
            remoteDataSource = get(),
            rateLimiter = get()
        )
    }

    single<EventRepository> {
        EventRepositoryImpl(
            remoteDataSource = get(),
            eventDao = get(),
            fightDao = get(),
            dateTimeProvider = get(),
            rateLimiter = get()
        )
    }

    single<WeightClassRepository> {
        WeightClassRepositoryImpl(
            remoteDataSource = get(),
            dao = get(),
            rateLimiter = get()
        )
    }

    single<FighterRepository> {
        FighterRepositoryImpl(
            fighterRemoteDataSource = get(),
            fightRemoteDataSource = get(),
            fighterDao = get(),
            fightDao = get(),
            rateLimiter = get()
        )
    }

    single<AuthRepository> {
        AuthRepositoryImpl(
            supabaseClient = get(),
            deviceTokenRemoteDataSource = get(),
            deviceTokenProvider = get(),
            scope = get(named("applicationScope"))
        )
    }

    single<UserRepository> {
        UserRepositoryImpl(
            remoteDataSource = get(),
            dao = get(),
            rateLimiter = get()
        )
    }

    single<LeaderboardRepository> {
        LeaderboardRepositoryImpl(
            remoteDataSource = get(),
            userDao = get(),
            weeklyLeaderboardDao = get(),
            rateLimiter = get()
        )
    }

    single<NotificationRepository> {
        NotificationRepositoryImpl(
            remoteDataSource = get(),
            dao = get(),
            rateLimiter = get()
        )
    }

    single<PredictionRepository> {
        PredictionRepositoryImpl(
            predictionDao = get(),
            fightDao = get(),
            remoteDataSource = get(),
            rateLimiter = get()
        )
    }

    single<InteractionRepository> {
        InteractionRepositoryImpl(
            remoteDataSource = get(),
            interactionDao = get(),
            fighterDao = get(),
            rateLimiter = get()
        )
    }

    single<AppConfigRepository> {
        AppConfigRepositoryImpl(
            remoteDataSource = get(),
            configDao = get(),
            rateLimiter = get()
        )
    }

    // view model
    viewModel {
        HomeViewModel(
            eventRepository = get(),
            dateTimeProvider = get(),
            appVersionRepository = get()
        )
    }

    viewModel {
        EventDetailViewModel(
            eventRepository = get(),
            savedStateHandle = get()
        )
    }

    viewModel {
        FightDetailViewModel(
            authRepository = get(),
            notificationRepository = get(),
            predictionRepository = get(),
            notificationStorage = get(),
            savedStateHandle = get(),
            fightRepository = get()
        )
    }

    viewModel {
        RankingViewModel(repository = get())
    }

    viewModel {
        RankingDetailViewModel(
            repository = get(),
            savedStateHandle = get()
        )
    }

    viewModel {
        FighterDetailViewModel(
            repository = get<FighterRepository>(),
            savedStateHandle = get(),
            authRepository = get<AuthRepository>(),
            interactionRepository = get<InteractionRepository>()
        )
    }

    viewModel {
        FighterSearchViewModel(
            fighterRepository = get(),
            weightClassRepository = get(),
            interactionRepository = get(),
            authRepository = get(),
            savedStateHandle = get()
        )
    }

    viewModel {
        UserSearchViewModel(
            userRepository = get()
        )
    }

    viewModel {
        ProfileViewModel(
            userRepository = get(),
            authRepository = get(),
            predictionRepository = get(),
            interactionRepository = get(),
            savedStateHandle = get()
        )
    }

    viewModel {
        MenuViewModel(
            authRepository = get(),
            userRepository = get(),
            predictionRepository = get(),
            notificationRepository = get()
        )
    }

    viewModel {
        ProfileEditViewModel(
            userRepository = get(),
            authRepository = get(),
        )
    }

    viewModel {
        InteractionListViewModel(
            authRepository = get(),
            interactionRepository = get(),
            savedStateHandle = get()
        )
    }

    viewModel {
        LeaderboardViewModel(
            leaderboardRepository = get(),
            authRepository = get(),
            configRepository = get(),
            languageStorage = get()
        )
    }

    viewModel {
        BlockedUsersViewModel(
            userRepository = get(),
            authRepository = get()
        )
    }

    viewModel {
        SettingsViewModel(
            authRepository = get()
        )
    }
}
