package com.simplepro.todoandmemooffline.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import com.simplepro.todoandmemooffline.R
import com.simplepro.todoandmemooffline.activity.MainActivity

class AlarmReceiver : BroadcastReceiver() {

    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel : NotificationChannel
    lateinit var builder : Notification.Builder
    private val channelId = "com.simplepro.todoandmemooffline"
    private val description = "Test notification"

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("TAG", "receive")
        if(intent!!.hasExtra("todoText"))
        {
            Log.d("TAG", "hasExtra")
            val todoText = intent!!.getStringExtra("todoText")
            val contentView = RemoteViews(context!!.packageName, R.layout.todo_notification_layout)

            contentView.setTextViewText(R.id.notificationTodoTitleTextView, todoText)
            notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivities(context, 0, arrayOf(intent), PendingIntent.FLAG_UPDATE_CURRENT)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.GREEN
                notificationChannel.enableVibration(true)
                notificationManager.createNotificationChannel(notificationChannel)

                builder = Notification.Builder(context, channelId)
                    .setContent(contentView)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.app_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.app_logo))
                    .setContentIntent(pendingIntent)
            } else {
                builder = Notification.Builder(context)
                    .setContent(contentView)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.app_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.app_logo))
                    .setContentIntent(pendingIntent)
            }
            notificationManager.notify(1234, builder.build())
        }
    }
}