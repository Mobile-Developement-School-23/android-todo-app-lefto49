package com.todoapplication.view.activity

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.sqlite.db.SupportSQLiteCompat.Api16Impl.cancel
import com.google.android.material.snackbar.Snackbar
import com.todoapplication.R
import com.todoapplication.TodoApp
import com.todoapplication.data.entity.Importance
import com.todoapplication.data.entity.TodoItem
import com.todoapplication.util.background.NetworkChangeReceiver
import com.todoapplication.util.background.NotificationReceiver
import com.todoapplication.view.model.TaskViewModel
import com.todoapplication.view.model.ViewModelFactory
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * A main activity of the application.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var rootLayout: LinearLayout

    @Inject
    lateinit var receiver: NetworkChangeReceiver

    @Inject
    lateinit var prefs: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as TodoApp).appComponent.activityComponent().inject(this)
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]
        actionBar?.hide()
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.f_main)
        rootLayout = findViewById<LinearLayout>(R.id.root)

        lifecycleScope.launch {
            viewModel.getSnackBar().flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect {
                if (it != null) {
                    Snackbar.make(rootLayout, it, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        setTheme(prefs.getString("theme", "system")!!)
        registerReceiver(
            receiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notChan = NotificationChannel("1", "CHANNEL", NotificationManager.IMPORTANCE_HIGH)
            val nManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.createNotificationChannel(notChan)
        }
    }

    fun changeTaskDone(task: TodoItem, isDone: Boolean) {
        task.isDone = isDone
        viewModel.updateTask(task)
    }

    fun deleteTask(task: TodoItem) {
        val snackBar = Snackbar.make(rootLayout, "Text", Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction("Отмена") {
            snackBar.dismiss()
        }
        snackBar.show()
        object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                snackBar.setText("Удалить ${task.task} ${millisUntilFinished / 1000 + 1}")
            }

            override fun onFinish() {
                if (snackBar.isShown) {
                    viewModel.deleteTask(task)
                    snackBar.dismiss()
                    removeNotification(task.id)
                }
            }
        }.start()
    }

    fun removeNotification(taskId: String) {
        val notId = prefs.getInt(taskId, Int.MIN_VALUE)
        if (notId != Int.MIN_VALUE) {
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(notId)
            prefs.edit().remove(taskId).apply()
        }
    }

    fun createNotification(task: TodoItem) {
        var notId = prefs.getInt("lastNotId", Int.MIN_VALUE)

        ++notId
        if (notId == Int.MIN_VALUE) {
            ++notId
        }

        prefs.edit().putInt("lastNotId", notId).putInt(task.id, notId).apply()

        val importance = when (task.importance) {
            Importance.important -> resources.getString(R.string.high)
            Importance.low -> resources.getString(R.string.low)
            else -> resources.getString(R.string.no)
        }
        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("taskId", task.id)
            putExtra("importance", importance)
            putExtra("taskName", task.task)
            putExtra("notId", notId)
        }

        val pendIntent =
            PendingIntent.getBroadcast(
                this,
                Date().time.toInt(),
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

        manager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            task.deadline!!.time,
            pendIntent
        )
    }

    fun setTheme(theme: String) {
        prefs.edit().putString("theme", theme).apply()
        when (theme) {
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}