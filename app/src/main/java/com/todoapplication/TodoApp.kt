package com.todoapplication

import android.app.Application
import android.content.SharedPreferences
import androidx.room.Room
import androidx.work.*
import com.todoapplication.data.room.TodoDatabase
import com.todoapplication.data.repository.TodoItemsRepository
import com.todoapplication.util.background.UploadManager
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class TodoApp : Application() {
    companion object {
        private lateinit var instance: TodoApp
        public lateinit var repo: TodoItemsRepository
        public val formatter = SimpleDateFormat("dd.MM.yyyy")
        public fun getInstance(): TodoApp {
            return instance
        }

        public lateinit var preferences: SharedPreferences
    }

    private lateinit var db: TodoDatabase

    public override fun onCreate() {
        super.onCreate()
        instance = this
        db = Room.databaseBuilder(this, TodoDatabase::class.java, "db").build()
        preferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE)
        repo = TodoItemsRepository()

        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val periodicWorker =
            PeriodicWorkRequestBuilder<UploadManager>(repeatInterval = 8, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 5, TimeUnit.MINUTES).build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "Periodic",
                ExistingPeriodicWorkPolicy.UPDATE, periodicWorker
            )
    }

    fun getDatabase(): TodoDatabase = db
}
