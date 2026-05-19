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
import com.berkekucuk.mmaapp.data.remote.api.DeviceTokenRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.api.DeviceTokenSupabaseAPI
import com.berkekucuk.mmaapp.data.remote.api.EventRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.api.EventSupabaseAPI
import com.berkekucuk.mmaapp.data.remote.api.FightRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.api.FightSupabaseAPI
import com.berkekucuk.mmaapp.data.remote.api.FighterRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.api.FighterSupabaseAPI
import com.berkekucuk.mmaapp.data.remote.api.InteractionRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.api.InteractionSupabaseAPI
import com.berkekucuk.mmaapp.data.remote.api.WeightClassRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.api.WeightClassSupabaseAPI
import com.berkekucuk.mmaapp.data.remote.api.UserRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.api.UserSupabaseAPI
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
import com.berkekucuk.mmaapp.data.remote.api.NotificationRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.api.NotificationSupabaseAPI
import com.berkekucuk.mmaapp.data.remote.api.PredictionRemoteDataSource
import com.berkekucuk.mmaapp.data.remote.api.PredictionSupabaseAPI
import com.berkekucuk.mmaapp.data.repository.FightRepositoryImpl
import com.berkekucuk.mmaapp.data.repository.InteractionRepositoryImpl
import com.berkekucuk.mmaapp.domain.repository.EventRepository
import com.berkekucuk.mmaapp.domain.repository.FightRepository
import com.berkekucuk.mmaapp.domain.repository.FighterRepository
import com.berkekucuk.mmaapp.domain.repository.InteractionRepository
import com.berkekucuk.mmaapp.domain.repository.WeightClassRepository
import com.berkekucuk.mmaapp.domain.repository.UserRepository
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
import com.berkekucuk.mmaapp.presentation.screens.leaderboard.LeaderboardViewModel
import com.berkekucuk.mmaapp.presentation.screens.settings.SettingsViewModel
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
            remoteDataSource = get(),
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

    // view model
    viewModel {
        HomeViewModel(
            eventRepository = get(),
            dateTimeProvider = get()
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
            fighterRepository = get(),
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
            repository = get(),
            savedStateHandle = get()
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
        ProfileViewModel(
            userRepository = get(),
            authRepository = get(),
            notificationRepository = get(),
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
            userRepository = get(),
            authRepository = get()
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
