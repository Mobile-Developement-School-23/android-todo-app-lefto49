package com.todoapplication.data.repository

import android.util.Log
import com.todoapplication.TodoApp
import com.todoapplication.data.entity.JsonConverters
import com.todoapplication.data.entity.TodoItem
import com.todoapplication.data.network.api.ResponseStatus
import com.todoapplication.data.network.api.TodoAPI
import com.todoapplication.data.network.interaction.*
import com.todoapplication.data.room.TodoDatabase
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class TodoItemsRepository {
    private var db: TodoDatabase = TodoApp.getInstance().getDatabase()

    private var revision = TodoApp.preferences.getInt("revision", 0)

    private val httpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
    private val apiClient = Retrofit.Builder().baseUrl("https://beta.mrdekk.ru/todobackend/")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create()).build().create(TodoAPI::class.java)

    suspend fun syncData(): ResponseStatus {
        if (TodoApp.preferences.getBoolean("local updates", false)) {
            return patchData()
        }
        return getData()
    }

    private suspend fun getData(): ResponseStatus {
        var response: Response<TaskListResponse>? = null

        try {
            response =
                apiClient.getTasks()
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
                apiClient.updateTasks(revision, request)
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
                apiClient.updateTask(task.id, request, revision)
        } catch (e: Exception) {
            Log.println(Log.ERROR, "Exception", e.message.toString())
        }

        if (response == null) {
            db.todoDao().addTask(task)
            return ResponseStatus.FAILED
        }

        if (response.code() == 200 && response.body() != null) {
            val result = response.body()!!
            db.todoDao().addTask(JsonConverters.fromRemote(result.task))
            revision = result.revision
            return ResponseStatus.OK
        }

        Log.println(Log.ERROR, "TAG", response.message())
        if (response.code() == 400 && response.body()!!.status.equals("unsynchronized data")) {
            return ResponseStatus.UNSYNC
        }

        return ResponseStatus.ERROR
    }

    suspend fun deleteTask(task: TodoItem): ResponseStatus {
        var response: Response<SingleTaskResponse>? = null

        try {
            response =
                apiClient.deleteTask(task.id, revision)
        } catch (e: Exception) {
            Log.println(Log.ERROR, "Exception", e.message.toString())
        }

        if (response == null) {
            db.todoDao().deleteTask(task)
            return ResponseStatus.FAILED
        }

        if (response.code() == 200 && response.body() != null) {
            val result = response.body()!!
            db.todoDao().deleteTask(JsonConverters.fromRemote(result.task))
            revision = result.revision
            return ResponseStatus.OK
        }

        Log.println(Log.ERROR, "TAG", response.message())
        if (response.code() == 400 && response.body()!!.status.equals("unsynchronized data")) {
            return ResponseStatus.UNSYNC
        }
        return ResponseStatus.ERROR
    }

    suspend fun addTask(task: TodoItem): ResponseStatus {
        var response: Response<SingleTaskResponse>? = null
        try {
            response =
                apiClient.addTask(revision, AddTaskRequest(JsonConverters.toRemote(task)))
        } catch (e: Exception) {
            Log.println(Log.ERROR, "Exception", e.message.toString())
        }

        if (response == null) {
            db.todoDao().addTask(task)
            return ResponseStatus.FAILED
        }

        if (response.code() == 200 && response.body() != null) {
            val result = response.body()!!
            db.todoDao().addTask(JsonConverters.fromRemote(result.task))
            revision = result.revision
            return ResponseStatus.OK
        }

        Log.println(Log.ERROR, "TAG", response.message())
        if (response.code() == 400 && response.body()!!.status.equals("unsynchronized data")) {
            return ResponseStatus.UNSYNC
        }
        return ResponseStatus.ERROR
    }

    fun getRevision(): Int = revision

    fun getAll(): Flow<List<TodoItem>> = db.todoDao().getAll()

    fun getTaskById(taskId: String): Flow<TodoItem> = db.todoDao().getTaskById(taskId)
}
