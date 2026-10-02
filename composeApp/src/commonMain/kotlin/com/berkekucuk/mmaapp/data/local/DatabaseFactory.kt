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
import com.berkekucuk.mmaapp.data.local.dao.FightStatDao
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
import com.berkekucuk.mmaapp.data.local.entity.FightStatEntity
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
        FightStatEntity::class,
        FighterFightCrossRef::class,
        InteractionEntity::class,
        BlockedUserEntity::class,
        AppConfigEntity::class,
        WeeklyLeaderboardEntity::class
    ],
    version = 36
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

val MIGRATION_34_35 = object : Migration(34, 35) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE `fights` ADD COLUMN `title_type` TEXT")
        connection.execSQL("ALTER TABLE `fights` ADD COLUMN `referee` TEXT")
        connection.execSQL("ALTER TABLE `fights` ADD COLUMN `bonuses` TEXT")
        connection.execSQL("ALTER TABLE `fighters` ADD COLUMN `slpm` REAL")
        connection.execSQL("ALTER TABLE `fighters` ADD COLUMN `str_acc` REAL")
        connection.execSQL("ALTER TABLE `fighters` ADD COLUMN `sapm` REAL")
        connection.execSQL("ALTER TABLE `fighters` ADD COLUMN `str_def` REAL")
        connection.execSQL("ALTER TABLE `fighters` ADD COLUMN `td_avg` REAL")
        connection.execSQL("ALTER TABLE `fighters` ADD COLUMN `td_acc` REAL")
        connection.execSQL("ALTER TABLE `fighters` ADD COLUMN `td_def` REAL")
        connection.execSQL("ALTER TABLE `fighters` ADD COLUMN `sub_avg` REAL")
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `fight_stats` (
                `fight_id` TEXT NOT NULL,
                `fighter_id` TEXT NOT NULL,
                `round` INTEGER NOT NULL,
                `knockdowns` INTEGER,
                `sig_strikes_landed` INTEGER,
                `sig_strikes_attempted` INTEGER,
                `total_strikes_landed` INTEGER,
                `total_strikes_attempted` INTEGER,
                `takedowns_landed` INTEGER,
                `takedowns_attempted` INTEGER,
                `submission_attempts` INTEGER,
                `reversals` INTEGER,
                `control_time_seconds` INTEGER,
                `head_landed` INTEGER,
                `head_attempted` INTEGER,
                `body_landed` INTEGER,
                `body_attempted` INTEGER,
                `leg_landed` INTEGER,
                `leg_attempted` INTEGER,
                `distance_landed` INTEGER,
                `distance_attempted` INTEGER,
                `clinch_landed` INTEGER,
                `clinch_attempted` INTEGER,
                `ground_landed` INTEGER,
                `ground_attempted` INTEGER,
                PRIMARY KEY(`fight_id`, `fighter_id`, `round`)
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_35_36 = object : Migration(35, 36) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE `weight_classes` ADD COLUMN `weight_limit` INTEGER")

        // Rebuild fights table so method_type, method_detail, round_summary match nullable schema in Room 36
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `fights_new` (
                `fight_id` TEXT NOT NULL,
                `event_id` TEXT NOT NULL,
                `event_name` TEXT,
                `event_date` INTEGER,
                `method_type` TEXT,
                `method_detail` TEXT,
                `round_summary` TEXT,
                `bout_type` TEXT NOT NULL,
                `weight_class_lbs` INTEGER,
                `weight_class_id` TEXT NOT NULL,
                `rounds_format` TEXT NOT NULL,
                `fight_order` INTEGER NOT NULL,
                `title_type` TEXT,
                `referee` TEXT,
                `bonuses` TEXT,
                `participants` TEXT NOT NULL,
                PRIMARY KEY(`fight_id`)
            )
            """.trimIndent()
        )
        connection.execSQL(
            """
            INSERT INTO `fights_new` (
                `fight_id`, `event_id`, `event_name`, `event_date`,
                `method_type`, `method_detail`, `round_summary`,
                `bout_type`, `weight_class_lbs`, `weight_class_id`,
                `rounds_format`, `fight_order`, `title_type`,
                `referee`, `bonuses`, `participants`
            )
            SELECT 
                `fight_id`, `event_id`, `event_name`, `event_date`,
                `method_type`, `method_detail`, `round_summary`,
                `bout_type`, `weight_class_lbs`, `weight_class_id`,
                `rounds_format`, `fight_order`, `title_type`,
                `referee`, `bonuses`, `participants`
            FROM `fights`
            """.trimIndent()
        )
        connection.execSQL("DROP TABLE `fights`")
        connection.execSQL("ALTER TABLE `fights_new` RENAME TO `fights`")
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
            MIGRATION_33_34,
            MIGRATION_34_35,
            MIGRATION_35_36
        )
        .fallbackToDestructiveMigration(true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
