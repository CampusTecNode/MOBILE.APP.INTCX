package com.intec.connect.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.intec.connect.R
import com.intec.connect.ui.activities.BottomNavigationActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")

            val title = it.title ?: getString(R.string.app_name)
            val body = it.body ?: ""
            sendNotification(title, body)
            saveNotificationToJsonFile(title, body) // Save to JSON file
        }
    }

    private fun saveNotificationToJsonFile(title: String?, messageBody: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val notificationData = mapOf(
                    "title" to title,
                    "body" to messageBody,
                    "timestamp" to System.currentTimeMillis()
                )

                val gson = Gson()
                val jsonData = gson.toJson(notificationData)

                // Get SharedPreferences
                val sharedPrefs = getSharedPreferences("notifications", Context.MODE_PRIVATE)
                val editor = sharedPrefs.edit()

                // Create a unique key for the notification (e.g., using timestamp)
                val key = "notification_${System.currentTimeMillis()}"

                // Store the JSON data in SharedPreferences
                editor.putString(key, jsonData)
                editor.apply()

                Log.d(TAG, "Notification saved to SharedPreferences with key: $key")

            } catch (e: Exception) {
                Log.e(TAG, "Error saving notification to SharedPreferences: ${e.message}", e)
            }
        }
    }

    private fun sendNotification(title: String?, messageBody: String?) {
        val intent = Intent(this, BottomNavigationActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE
                    or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)


        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,

                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMessagingService"
    }

}