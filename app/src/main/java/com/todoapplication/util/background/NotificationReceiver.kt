package com.todoapplication.util.background

import android.Manifest
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDeepLinkBuilder
import com.todoapplication.R
import com.todoapplication.view.activity.MainActivity
import java.util.*

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) {
            return
        }

        val notId = intent?.extras?.getInt("notId") ?: return
        val taskId = intent.extras?.getString("taskId") ?: return
        val importance = intent.extras?.getString("importance") ?: return
        val taskName = intent.extras?.getString("taskName") ?: return

        val postponeIntent = Intent(context, NotificationResultReceiver::class.java).apply {
            putExtra("notId", notId)
            putExtra("taskId", taskId)
            putExtra("importance", importance)
            putExtra("taskName", taskName)
            putExtra("time", Date().time)
        }

        val openIntent = Intent(context, MainActivity::class.java)

        val notification = NotificationCompat.Builder(context, "1")
            .setContentText("Наступил дедлайн дела $taskName.\nВажность дела: $importance")
            .setSmallIcon(R.mipmap.icon)
            .setContentTitle("TodoApplication")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(
                android.R.drawable.ic_lock_idle_alarm,
                "Отложить",
                PendingIntent.getBroadcast(
                    context,
                    Date().time.toInt(),
                    postponeIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            ).setContentIntent(
                PendingIntent.getActivity(
                    context,
                    Date().time.toInt(),
                    openIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            )

        val manager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        manager.notify(notId, notification.build())
    }
}