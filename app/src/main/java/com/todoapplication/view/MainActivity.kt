package com.todoapplication.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.todoapplication.data.Importance
import com.todoapplication.R
import com.todoapplication.TodoApp
import com.todoapplication.data.TodoItem
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.f_main)
    }

    fun changeTaskDone(task: TodoItem, isDone: Boolean) {
        TodoApp.repo.changeTaskDone(task, isDone)
    }

    fun addTask(
        taskText: String,
        deadline: Date?,
        importance: Importance
    ) {
        val createdAt = Date()
        val task = TodoItem(
            UUID.randomUUID().toString(),
            taskText,
            importance,
            deadline,
            false,
            createdAt,
            createdAt
        )
        TodoApp.repo.addTask(task)
        navController.popBackStack()
    }

    fun updateTask(
        taskId: String, taskText: String,
        isDeadline: Boolean,
        deadline: String,
        importance: Importance
    ) {
        if (isDeadline) {
            TodoApp.repo.updateTask(taskId, taskText, TodoApp.formatter.parse(deadline), importance)
        } else {
            TodoApp.repo.updateTask(taskId, taskText, null, importance)
        }
        navController.popBackStack()
    }

    fun deleteTask(taskId: String) {
        TodoApp.repo.removeTask(taskId)
        navController.popBackStack()
    }

    fun popStack() {
        navController.popBackStack()
    }

    fun getTaskById(taskId: String): TodoItem {
        return TodoApp.repo.getTaskById(taskId)
    }

    fun showAddTaskFragment() {
        navController.navigate(R.id.action_taskListFragment_to_addTaskFragment)
    }

    fun showTaskInfo(taskId: String) {
        val bundle = Bundle()
        bundle.putString("taskId", taskId)
        navController.navigate(R.id.action_taskListFragment_to_taskInfoFragment, bundle)
    }
}