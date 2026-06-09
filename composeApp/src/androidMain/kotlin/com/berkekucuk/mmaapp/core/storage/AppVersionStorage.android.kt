package com.berkekucuk.mmaapp.core.storage

import android.content.Context
import androidx.core.content.edit

class AndroidAppVersionStorage(context: Context) : AppVersionStorage {
    private val prefs = context.getSharedPreferences("app_version_prefs", Context.MODE_PRIVATE)

    override fun saveMinRequiredVersion(version: Int) {
        prefs.edit { putInt("min_required_version", version) }
    }

    override fun getMinRequiredVersion(): Int {
        return prefs.getInt("min_required_version", 1)
    }
}
