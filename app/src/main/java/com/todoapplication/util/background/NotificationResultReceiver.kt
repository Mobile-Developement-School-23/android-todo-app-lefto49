package com.todoapplication.util.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*

class NotificationResultReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) {
            return
        }

        val notId = intent?.extras?.getInt("notId") ?: return
        val taskId = intent.extras?.getString("taskId") ?: return
        val importance = intent.extras?.getString("importance") ?: return
        val taskName = intent.extras?.getString("taskName") ?: return
        val time = intent.extras?.getLong("time") ?: return


    }
}