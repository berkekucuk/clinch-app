package com.berkekucuk.mmaapp.core.storage

interface AppVersionStorage {
    fun saveMinRequiredVersion(version: Int)
    fun getMinRequiredVersion(): Int
}
