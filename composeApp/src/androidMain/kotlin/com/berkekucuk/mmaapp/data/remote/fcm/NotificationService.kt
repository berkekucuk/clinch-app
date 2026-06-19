package com.berkekucuk.mmaapp.data.remote.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.berkekucuk.mmaapp.MainActivity
import com.berkekucuk.mmaapp.R
import android.media.RingtoneManager
import com.berkekucuk.mmaapp.core.presentation.AppLanguage
import com.berkekucuk.mmaapp.core.presentation.strings.AppStrings
import com.berkekucuk.mmaapp.core.presentation.strings.EnStrings
import com.berkekucuk.mmaapp.core.presentation.strings.TrStrings
import com.berkekucuk.mmaapp.core.storage.LanguageStorage
import kotlin.random.Random

class NotificationService(
    private val context: Context,
    private val languageStorage: LanguageStorage
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    private val strings: AppStrings
        get() = try {
            if (AppLanguage.valueOf(languageStorage.load()) == AppLanguage.TR) TrStrings else EnStrings
        } catch (_: Exception) {
            EnStrings
        }

    companion object {
        private const val CHANNEL_ID = "clinch_alerts_channel"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            strings.notificationChannelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = strings.notificationChannelDescription
            enableLights(true)
            enableVibration(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification(title: String, body: String, data: Map<String, String>) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            data.forEach { (key, value) -> putExtra(key, value) }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(Random.nextInt(), notification)
    }

    fun showAlarmNotification(matchup: String?, fightId: String?) {
        val isScreenOn = (context.getSystemService(Context.POWER_SERVICE) as PowerManager).isInteractive

        if (isScreenOn) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                if (fightId != null) putExtra("fight_id", fightId)
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(strings.alarmFightTime)
                .setContentText(matchup ?: "")
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .build()
            notificationManager.notify(1001, notification)
        } else {
            val intent = Intent(context, Class.forName("com.berkekucuk.mmaapp.presentation.alarm.AlarmActivity")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("matchup", matchup)
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 2, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(strings.alarmFightTime)
                .setContentText(matchup ?: "")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(pendingIntent, true)
                .setAutoCancel(true)
                .build()
            notificationManager.notify(1001, notification)
        }
    }
}
