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
import com.intec.connect.api.RetrofitApiClient
import com.intec.connect.repository.RetrofitRepository
import com.intec.connect.ui.activities.BottomNavigationActivity
import com.intec.connect.ui.notifications.NotificationsViewModel
import com.intec.connect.utilities.Constants
import com.intec.connect.utilities.Constants.TOKEN_KEY
import com.intec.connect.utilities.Constants.USERID_KEY
import com.intec.connect.utilities.NotificationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
            saveNotificationToJsonFile(title, body)

            // Enviar la notificación al backend
            sendNotificationToBackend(title, body)
        }

    }

    private fun sendNotificationToBackend(title: String?, body: String?) {

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val userAPI = retrofit.create(RetrofitApiClient::class.java)
        val repository = RetrofitRepository(userAPI)
        val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getString(USERID_KEY, "") ?: ""
        val token = sharedPrefs.getString(TOKEN_KEY, "") ?: ""

        if (userId.isNotEmpty() && token.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val notificationsViewModel = NotificationsViewModel(repository = repository)
                    notificationsViewModel.saveNotification(
                        title ?: "",
                        body ?: "",
                        userId,
                        NotificationType.NEW_PRODUCT.name,
                        token
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error sending notification to backend: ${e.message}", e)
                }
            }
        } else {
            Log.w(TAG, "User ID or token not found. Cannot send notification to backend.")
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

                val sharedPrefs = getSharedPreferences("notifications", Context.MODE_PRIVATE)
                val editor = sharedPrefs.edit()

                val key = "notification_${System.currentTimeMillis()}"

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