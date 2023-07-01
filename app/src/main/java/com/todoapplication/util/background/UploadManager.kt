package com.todoapplication.util.background

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.todoapplication.TodoApp
import com.todoapplication.data.network.api.ResponseStatus


class UploadManager(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val result = TodoApp.repo.syncData()
        if (result == ResponseStatus.OK) {
            TodoApp.preferences.edit().putBoolean("local updates", false).apply()
        }

        return when (result) {
            ResponseStatus.OK -> Result.success()
            ResponseStatus.ERROR -> Result.retry()
            else -> Result.failure()
        }
    }
}