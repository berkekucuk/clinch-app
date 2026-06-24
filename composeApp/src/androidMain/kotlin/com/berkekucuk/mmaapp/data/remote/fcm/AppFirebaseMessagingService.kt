package com.berkekucuk.mmaapp.data.remote.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppFirebaseMessagingService : FirebaseMessagingService(), KoinComponent {

    private val notificationManager: NotificationService by inject()

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val type = message.data["type"]

        when (type) {
            "MANUAL" -> {
                val title = message.notification?.title ?: message.data["title"]
                val body = message.notification?.body ?: message.data["body"]
                val eventId = message.data["event_id"]

                if (title != null && body != null) {
                    notificationManager.showManualNotification(
                        title = title,
                        body = body,
                        eventId = eventId
                    )
                }
            }

            "START" -> {
                val matchup = message.data["matchup"]
                val fightId = message.data["fight_id"]
                notificationManager.showStartNotification(matchup, fightId)
            }

            "ALARM" -> {
                val matchup = message.data["matchup"]
                val fightId = message.data["fight_id"]
                notificationManager.showAlarmNotification(matchup, fightId)
            }

            else -> {
                val title = message.notification?.title ?: message.data["title"]
                val body = message.notification?.body ?: message.data["body"]
                val eventId = message.data["event_id"]

                if (title != null && body != null) {
                    notificationManager.showManualNotification(
                        title = title,
                        body = body,
                        eventId = eventId
                    )
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Handle new token if needed
    }
}
