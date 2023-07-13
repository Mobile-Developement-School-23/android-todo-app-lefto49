package com.todoapplication.util.background

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.todoapplication.R
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

        val notification = NotificationCompat.Builder(context, "1")
            .setContentText("Наступил дедлайн дела $taskName.\nВажность дела: $importance")
            .setSmallIcon(R.mipmap.icon)
            .setContentTitle("TodoApplication")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val manager = NotificationManagerCompat.from(context)
        manager.notify(notId, notification.build())
    }
}
