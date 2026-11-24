package com.berkekucuk.mmaapp.data.local

import com.berkekucuk.mmaapp.data.local.dao.NewsDao
import com.berkekucuk.mmaapp.data.local.entity.NewsArticleEntity
import com.berkekucuk.mmaapp.core.utils.clearImageCache
import androidx.sqlite.SQLiteConnection
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.berkekucuk.mmaapp.data.local.dao.EventDao
import com.berkekucuk.mmaapp.data.local.dao.FightDao
import com.berkekucuk.mmaapp.data.local.dao.FightOddsDao
import com.berkekucuk.mmaapp.data.local.dao.FightStatDao
import com.berkekucuk.mmaapp.data.local.dao.FighterDao
import com.berkekucuk.mmaapp.data.local.dao.InteractionDao
import com.berkekucuk.mmaapp.data.local.dao.NotificationDao
import com.berkekucuk.mmaapp.data.local.dao.PredictionDao
import com.berkekucuk.mmaapp.data.local.dao.RankingDao
import com.berkekucuk.mmaapp.data.local.dao.UserDao
import com.berkekucuk.mmaapp.data.local.dao.WeeklyLeaderboardDao
import com.berkekucuk.mmaapp.data.local.entity.BlockedUserEntity
import com.berkekucuk.mmaapp.data.local.entity.EventEntity
import com.berkekucuk.mmaapp.data.local.entity.FightEntity
import com.berkekucuk.mmaapp.data.local.entity.FightNotificationEntity
import com.berkekucuk.mmaapp.data.local.entity.FightOddsEntity
import com.berkekucuk.mmaapp.data.local.entity.FightStatEntity
import com.berkekucuk.mmaapp.data.local.entity.FighterEntity
import com.berkekucuk.mmaapp.data.local.entity.FighterFightCrossRef
import com.berkekucuk.mmaapp.data.local.entity.InteractionEntity
import com.berkekucuk.mmaapp.data.local.entity.PredictionEntity
import com.berkekucuk.mmaapp.data.local.entity.SyncedYearEntity
import com.berkekucuk.mmaapp.data.local.entity.UserEntity
import com.berkekucuk.mmaapp.data.local.entity.WeeklyLeaderboardEntity
import com.berkekucuk.mmaapp.data.local.entity.WeightClassEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [
        EventEntity::class,
        WeightClassEntity::class,
        FighterEntity::class,
        UserEntity::class,
        SyncedYearEntity::class,
        PredictionEntity::class,
        FightNotificationEntity::class,
        FightEntity::class,
        FightStatEntity::class,
        FightOddsEntity::class,
        FighterFightCrossRef::class,
        InteractionEntity::class,
        BlockedUserEntity::class,
        WeeklyLeaderboardEntity::class,
        NewsArticleEntity::class
    ],
    version = 43,
)

@TypeConverters(Converters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun rankingDao(): RankingDao
    abstract fun fighterDao(): FighterDao
    abstract fun userDao(): UserDao
    abstract fun notificationDao(): NotificationDao
    abstract fun predictionDao(): PredictionDao
    abstract fun fightDao(): FightDao
    abstract fun fightStatDao(): FightStatDao
    abstract fun fightOddsDao(): FightOddsDao
    abstract fun interactionDao(): InteractionDao
    abstract fun weeklyLeaderboardDao(): WeeklyLeaderboardDao
    abstract fun newsDao(): NewsDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .fallbackToDestructiveMigration(true)
        .addCallback(object : RoomDatabase.Callback() {
            override fun onDestructiveMigration(connection: SQLiteConnection) {
                runCatching { clearImageCache() }
            }
        })
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
