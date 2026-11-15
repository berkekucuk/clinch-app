package com.berkekucuk.mmaapp

import com.berkekucuk.mmaapp.core.app.AppStartupState
import android.os.SystemClock
import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.berkekucuk.mmaapp.core.app.App
import com.berkekucuk.mmaapp.core.app.DeepLinkManager
import com.berkekucuk.mmaapp.core.storage.NotificationStorage
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val notificationStorage: NotificationStorage by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            notificationStorage.setRequestedPermission(true)
        }

        val splashStartTime = SystemClock.uptimeMillis()
        splashScreen.setKeepOnScreenCondition {
            !AppStartupState.isReady.value &&
                SystemClock.uptimeMillis() - splashStartTime < AppStartupState.MAX_SPLASH_DURATION_MS
        }
        handleIntent(intent)

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val fightId = intent.getStringExtra("fight_id")
        if (fightId != null) {
            DeepLinkManager.navigateToFight(fightId)
            return
        }
        val eventId = intent.getStringExtra("event_id")
        if (eventId != null) {
            DeepLinkManager.navigateToEvent(eventId)
        }
    }
}
