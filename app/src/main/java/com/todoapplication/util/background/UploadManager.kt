package com.todoapplication.util.background

import android.content.Context
import android.content.SharedPreferences
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.todoapplication.data.network.api.ResponseStatus
import com.todoapplication.data.repository.TodoItemsRepository
import javax.inject.Inject

/**
 * Is used for periodic syncing of the local data with the remote data source.
 */
class UploadManager @Inject constructor(
    context: Context,
    params: WorkerParameters,
    private val repo: TodoItemsRepository, private val preferences: SharedPreferences
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val result = repo.syncData()
        if (result == ResponseStatus.OK) {
            preferences.edit().putBoolean("local updates", false).apply()
        }

        return when (result) {
            ResponseStatus.OK -> Result.success()
            ResponseStatus.ERROR -> Result.retry()
            else -> Result.failure()
        }
    }
}