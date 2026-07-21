package com.berkekucuk.mmaapp.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.berkekucuk.mmaapp.data.local.dao.EventDao
import com.berkekucuk.mmaapp.data.local.dao.FighterDao
import com.berkekucuk.mmaapp.data.local.dao.UserDao
import com.berkekucuk.mmaapp.data.local.dao.RankingDao
import com.berkekucuk.mmaapp.data.local.dao.NotificationDao
import com.berkekucuk.mmaapp.data.local.dao.PredictionDao
import com.berkekucuk.mmaapp.data.local.dao.FightDao
import com.berkekucuk.mmaapp.data.local.dao.InteractionDao
import com.berkekucuk.mmaapp.data.local.dao.AppConfigDao
import com.berkekucuk.mmaapp.data.local.dao.WeeklyLeaderboardDao
import com.berkekucuk.mmaapp.data.local.entity.EventEntity
import com.berkekucuk.mmaapp.data.local.entity.FightNotificationEntity
import com.berkekucuk.mmaapp.data.local.entity.PredictionEntity
import com.berkekucuk.mmaapp.data.local.entity.FighterEntity
import com.berkekucuk.mmaapp.data.local.entity.UserEntity
import com.berkekucuk.mmaapp.data.local.entity.SyncedYearEntity
import com.berkekucuk.mmaapp.data.local.entity.WeightClassEntity
import com.berkekucuk.mmaapp.data.local.entity.FighterFightCrossRef
import com.berkekucuk.mmaapp.data.local.entity.FightEntity
import com.berkekucuk.mmaapp.data.local.entity.InteractionEntity
import com.berkekucuk.mmaapp.data.local.entity.BlockedUserEntity
import com.berkekucuk.mmaapp.data.local.entity.AppConfigEntity
import com.berkekucuk.mmaapp.data.local.entity.WeeklyLeaderboardEntity
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
        FighterFightCrossRef::class,
        InteractionEntity::class,
        BlockedUserEntity::class,
        AppConfigEntity::class,
        WeeklyLeaderboardEntity::class
    ],
    version = 34
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
    abstract fun interactionDao(): InteractionDao
    abstract fun appConfigDao(): AppConfigDao
    abstract fun weeklyLeaderboardDao(): WeeklyLeaderboardDao
}

val MIGRATION_28_29 = object : Migration(28, 29) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `app_configs` (
                `key` TEXT NOT NULL, 
                `valueEn` TEXT, 
                `valueTr` TEXT, 
                PRIMARY KEY(`key`)
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_29_30 = object : Migration(29, 30) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE `users` ADD COLUMN `created_at` INTEGER")
    }
}

val MIGRATION_30_31 = object : Migration(30, 31) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `weekly_leaderboard` (
                `event_id` TEXT NOT NULL, 
                `user_id` TEXT NOT NULL, 
                `username` TEXT, 
                `full_name` TEXT, 
                `avatar_url` TEXT, 
                `weekly_points` INTEGER NOT NULL, 
                `created_at` INTEGER, 
                PRIMARY KEY(`event_id`, `user_id`)
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_31_32 = object : Migration(31, 32) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE `users` RENAME COLUMN `total_points` TO `points`")
    }
}

val MIGRATION_32_33 = object : Migration(32, 33) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE `fighters` ADD COLUMN `win_rate` REAL")
        connection.execSQL("ALTER TABLE `fighters` ADD COLUMN `ko_tko_rate` REAL")
        connection.execSQL("ALTER TABLE `fighters` ADD COLUMN `submission_rate` REAL")
    }
}

val MIGRATION_33_34 = object : Migration(33, 34) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE `events` ADD COLUMN `datetime_utc_main` INTEGER")
    }
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .addMigrations(
            MIGRATION_28_29,
            MIGRATION_29_30,
            MIGRATION_30_31,
            MIGRATION_31_32,
            MIGRATION_32_33,
            MIGRATION_33_34
        )
        .fallbackToDestructiveMigration(true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
