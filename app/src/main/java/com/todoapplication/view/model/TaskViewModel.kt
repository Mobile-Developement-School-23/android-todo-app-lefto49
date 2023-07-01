package com.todoapplication.view.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapplication.TodoApp
import com.todoapplication.data.entity.TodoItem
import com.todoapplication.data.network.api.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class TaskViewModel : ViewModel() {
    private lateinit var allTasks: Flow<List<TodoItem>>
    private lateinit var tasksList: List<TodoItem>
    private var snackbarText = MutableStateFlow<String?>(null)
    private val repo = TodoApp.repo

    init {
        uploadData()
    }

    fun uploadData() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.syncData()
            if (result != ResponseStatus.OK) {
                snackbarText.emit("Ошибка синхронизации. Отображаются локальные данные")
            }
        }
        allTasks = repo.getAll()
        viewModelScope.launch(Dispatchers.IO) {
            allTasks.collect {
                tasksList = it
            }
        }
    }

    fun getTasksList(): List<TodoItem> = tasksList
    fun getTasks(): Flow<List<TodoItem>> = allTasks

    fun updateTask(task: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.updateTask(task)

            if (result == ResponseStatus.OK) {
                return@launch
            }

            if (result == ResponseStatus.UNSYNC && repo.syncData() == ResponseStatus.OK) {
                updateTask(task)
            } else {
                snackbarText.emit("Изменения сохранены локально. Проверьте соединение и попробуйте позднее")
                TodoApp.preferences.edit().putBoolean("local updates", true).apply()
            }
        }
    }

    fun deleteTask(task: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.deleteTask(task)

            if (result == ResponseStatus.OK) {
                return@launch
            }

            if (result == ResponseStatus.UNSYNC && repo.syncData() == ResponseStatus.OK) {
                deleteTask(task)
            } else {
                snackbarText.emit("Изменения сохранены локально. Проверьте соединение и попробуйте позднее")
                TodoApp.preferences.edit().putBoolean("local updates", true).apply()
            }
        }
    }

    fun addTask(task: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.addTask(task)

            if (result == ResponseStatus.OK) {
                return@launch
            }

            if (result == ResponseStatus.UNSYNC && repo.syncData() == ResponseStatus.OK) {
                addTask(task)
            } else {
                snackbarText.emit("Изменения сохранены локально. Проверьте соединение и попробуйте позднее")
                TodoApp.preferences.edit().putBoolean("local updates", true).apply()
            }
        }
    }

    fun getSnackBar(): Flow<String?> = snackbarText

    fun getTaskById(taskId: String): Flow<TodoItem> = repo.getTaskById(taskId)

    override fun onCleared() {
        TodoApp.preferences.edit().putInt("revision", repo.getRevision()).apply()
        super.onCleared()
    }
}