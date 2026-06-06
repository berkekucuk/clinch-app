package com.berkekucuk.mmaapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.berkekucuk.mmaapp.core.app.App
import com.berkekucuk.mmaapp.core.app.DeepLinkManager
import com.berkekucuk.mmaapp.presentation.screens.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        splashScreen.setKeepOnScreenCondition {
            viewModel.state.value.isLoading
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
