package com.berkekucuk.mmaapp.di

import androidx.room.RoomDatabase
import com.berkekucuk.mmaapp.core.storage.AppVersionStorage
import com.berkekucuk.mmaapp.core.storage.IosAppVersionStorage
import com.berkekucuk.mmaapp.core.storage.IosLanguageStorage
import com.berkekucuk.mmaapp.core.storage.IosMeasurementUnitStorage
import com.berkekucuk.mmaapp.core.storage.IosNotificationStorage
import com.berkekucuk.mmaapp.core.storage.IosOddsFormatStorage
import com.berkekucuk.mmaapp.core.storage.IosThemeStorage
import com.berkekucuk.mmaapp.core.storage.LanguageStorage
import com.berkekucuk.mmaapp.core.storage.NotificationStorage
import com.berkekucuk.mmaapp.core.storage.MeasurementUnitStorage
import com.berkekucuk.mmaapp.core.storage.OddsFormatStorage
import com.berkekucuk.mmaapp.core.storage.ThemeStorage
import com.berkekucuk.mmaapp.data.local.AppDatabase
import com.berkekucuk.mmaapp.data.local.getDatabaseBuilder
import com.berkekucuk.mmaapp.data.remote.fcm.DeviceTokenProvider
import com.berkekucuk.mmaapp.data.remote.fcm.IosDeviceTokenProvider
import org.koin.dsl.module

actual val platformModule = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        getDatabaseBuilder()
    }
    single<LanguageStorage> {
        IosLanguageStorage()
    }
    single<AppVersionStorage> {
        IosAppVersionStorage()
    }
    single<MeasurementUnitStorage> {
        IosMeasurementUnitStorage()
    }
    single<OddsFormatStorage> {
        IosOddsFormatStorage()
    }
    single<NotificationStorage> {
        IosNotificationStorage()
    }
    single<ThemeStorage>{
        IosThemeStorage()
    }
    single<DeviceTokenProvider> {
        IosDeviceTokenProvider()
    }
}