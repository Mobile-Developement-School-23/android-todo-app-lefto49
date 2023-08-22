package com.todoapplication.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.todoapplication.data.entity.JsonConverters
import com.todoapplication.data.entity.TodoItem
import com.todoapplication.data.network.api.ResponseStatus
import com.todoapplication.data.network.api.TodoAPI
import com.todoapplication.data.network.interaction.*
import com.todoapplication.data.room.TodoDatabase
import com.todoapplication.di.annotation.ApplicationScope
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.util.*
import javax.inject.Inject

/**
 * Is used for managing the data and updating it locally and remotely.
 */
@ApplicationScope
class TodoItemsRepository @Inject constructor(
    private val db: TodoDatabase,
    private val retrofitClient: TodoAPI,
    private val preferences: SharedPreferences
) {
    private var revision = preferences.getInt("revision", 0)

    suspend fun syncData(): ResponseStatus {
        if (preferences.getBoolean("local updates", false)) {
            return patchData()
        }
        return getData()
    }

    private suspend fun getData(): ResponseStatus {
        var response: Response<TaskListResponse>? = null

        try {
            response =
                retrofitClient.getTasks()
        } catch (e: Exception) {
            Log.println(Log.ERROR, "Exception", e.message.toString())
            return ResponseStatus.FAILED
        }

        if (response.code() == 200 && response.body() != null) {
            val result = response.body()!!
            db.todoDao()
                .uploadInitialData(result.tasks.map { JsonConverters.fromRemote(it) })
            revision = result.revision
            return ResponseStatus.OK
        }

        Log.println(Log.ERROR, "TAG", response.message())
        return ResponseStatus.ERROR
    }

    suspend fun patchData(): ResponseStatus {
        val request = UpdateListRequest(
            db.todoDao().getAllList().map { JsonConverters.toRemote(it) })
        var response: Response<TaskListResponse>? = null

        try {
            response =
                retrofitClient.updateTasks(revision, request)
        } catch (e: Exception) {
            Log.println(Log.ERROR, "Exception", e.message.toString())
            return ResponseStatus.FAILED
        }

        if (response.code() == 200 && response.body() != null) {
            val result = response.body()!!
            db.todoDao()
                .uploadInitialData(result.tasks.map { JsonConverters.fromRemote(it) })
            revision = result.revision
            return ResponseStatus.OK
        }

        Log.println(Log.ERROR, "TAG", response.message())
        return ResponseStatus.ERROR
    }

    suspend fun updateTask(task: TodoItem): ResponseStatus {
        task.editedAt = Date()
        val request = AddTaskRequest(JsonConverters.toRemote(task))
        var response: Response<SingleTaskResponse>? = null
        try {
            response =
                retrofitClient.updateTask(task.id, request, revision)
        } catch (e: Exception) {
            Log.println(Log.ERROR, "Exception", e.message.toString())
        }

        if (response == null) {
            db.todoDao().addTask(task)
            return ResponseStatus.FAILED
        }

        if (response.code() >= 500) {
            db.todoDao().addTask(task)
        }

        if (response.code() == 200 && response.body() != null) {
            val result = response.body()!!
            db.todoDao().addTask(JsonConverters.fromRemote(result.task))
            revision = result.revision
            return ResponseStatus.OK
        }

        Log.println(Log.ERROR, "REVISION", response.message())
        if (response.code() == 400) {
            return ResponseStatus.UNSYNC
        }

        return ResponseStatus.ERROR
    }

    suspend fun deleteTask(task: TodoItem): ResponseStatus {
        var response: Response<SingleTaskResponse>? = null

        try {
            response =
                retrofitClient.deleteTask(task.id, revision)
        } catch (e: Exception) {
            Log.println(Log.ERROR, "Exception", e.message.toString())
        }

        if (response == null) {
            db.todoDao().deleteTask(task)
            return ResponseStatus.FAILED
        }

        if (response.code() >= 500) {
            db.todoDao().deleteTask(task)
        }

        if (response.code() == 200 && response.body() != null) {
            val result = response.body()!!
            db.todoDao().deleteTask(JsonConverters.fromRemote(result.task))
            revision = result.revision
            return ResponseStatus.OK
        }

        Log.println(Log.ERROR, "TAG", response.message())
        if (response.code() == 400) {
            return ResponseStatus.UNSYNC
        }
        return ResponseStatus.ERROR
    }

    suspend fun addTask(task: TodoItem): ResponseStatus {
        var response: Response<SingleTaskResponse>? = null
        try {
            response =
                retrofitClient.addTask(revision, AddTaskRequest(JsonConverters.toRemote(task)))
        } catch (e: Exception) {
            Log.println(Log.ERROR, "Exception", e.message.toString())
        }

        if (response == null) {
            db.todoDao().addTask(task)
            return ResponseStatus.FAILED
        }

        if (response.code() >= 500) {
            db.todoDao().addTask(task)
        }

        if (response.code() == 200 && response.body() != null) {
            val result = response.body()!!
            db.todoDao().addTask(JsonConverters.fromRemote(result.task))
            revision = result.revision
            return ResponseStatus.OK
        }
        Log.println(Log.ERROR, "REVISION", revision.toString())

        Log.println(Log.ERROR, "TAG", response.message())
        if (response.code() == 400) {
            return ResponseStatus.UNSYNC
        }
        return ResponseStatus.ERROR
    }

    fun getRevision(): Int = revision

    fun getAll(): Flow<List<TodoItem>> = db.todoDao().getAll()

    fun getTaskById(taskId: String): Flow<TodoItem> = db.todoDao().getTaskById(taskId)
}
