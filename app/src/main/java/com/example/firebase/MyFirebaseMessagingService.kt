package com.example.firebase

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.firebase.FirebaseConstant.channelId
import com.example.firebase.FirebaseConstant.channelName
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

   /* 1. Generate the notification
    2. Attach the notification created with the custom Layout
    3. Show the notification */

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnspecifiedImmutableFlag", "ObsoleteSdkInt")
    fun generateNotification(title :String, message :String) {
        val intent = Intent(this, WelcomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        //val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var builder : NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.id.imageFirebase)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setSound(notificationSoundUri)
            .setAutoCancel(true)
        builder = builder.setContent(getRemoveView(title,message))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(0,builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(message: RemoteMessage) {
        if (message.notification != null){
            generateNotification(message.notification!!.title!!, message.notification!!.body!!)
        }
    }
    @SuppressLint("RemoteViewLayout")
    private fun getRemoveView(title: String, remoteMessage: String): RemoteViews {
        val remoteView = RemoteViews("com.example.firebase", R.layout.activity_notification)
        remoteView.setTextViewText(R.id.textTitle, title)
        remoteView.setTextViewText(R.id.textDescription, remoteMessage)
        remoteView.setImageViewResource(R.id.imageFirebase,R.drawable.firebase)
        return remoteView
    }

    override fun onNewToken(token: String) {
        Log.d("FirebaseToken", "Refreshed token: $token")
        val newValue =  token
        print(newValue)
        Log.e("FirebaseToken", token)
    }
}