package com.todoapplication

import android.app.Application
import androidx.work.*

import com.todoapplication.di.component.AppComponent
import com.todoapplication.di.component.DaggerAppComponent
import com.todoapplication.util.background.UploadManager
import java.util.concurrent.TimeUnit

/**
 * Class describing an application.
 */
class TodoApp : Application() {
    val appComponent: AppComponent by lazy {
        initializeComponent()
    }

    private fun initializeComponent(): AppComponent {
        return DaggerAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()

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
}
