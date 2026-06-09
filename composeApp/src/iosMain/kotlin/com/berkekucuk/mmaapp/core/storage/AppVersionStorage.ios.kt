package com.berkekucuk.mmaapp.core.storage

import platform.Foundation.NSUserDefaults

class IosAppVersionStorage : AppVersionStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun saveMinRequiredVersion(version: Int) {
        defaults.setInteger(version.toLong(), forKey = "min_required_version")
    }

    override fun getMinRequiredVersion(): Int {
        val stored = defaults.integerForKey("min_required_version").toInt()
        return if (stored > 0) stored else 1
    }
}
