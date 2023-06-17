package com.todoapplication.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

class TodoItemsRepository {
    private val tasks: MutableLiveData<List<TodoItem>> = MutableLiveData()
    private val doneCounter: MutableLiveData<Int> = MutableLiveData(6)

    init {
        val tasksList = mutableListOf<TodoItem>()
        tasksList.add(
            TodoItem(
                UUID.randomUUID().toString(), "Do task", Importance.MIDDLE,
                Date(), true, Date(), Date()
            )
        )
        tasksList.add(
            TodoItem(
                UUID.randomUUID().toString(), "Do one more task", Importance.MIDDLE,
                Date(), false, Date(), Date()
            )
        )
        tasksList.add(
            TodoItem(
                UUID.randomUUID().toString(), "Do \nanother\ntask", Importance.MIDDLE,
                null, false, Date(), Date()
            )
        )
        tasksList.add(
            TodoItem(
                UUID.randomUUID().toString(), "Do task", Importance.MIDDLE,
                Date(), false, Date(), Date()
            )
        )
        tasksList.add(
            TodoItem(
                UUID.randomUUID().toString(), "Long\nLong\nLong\nLong", Importance.HIGH,
                Date(), false, Date(), Date()
            )
        )
        tasksList.add(
            TodoItem(
                UUID.randomUUID().toString(), "Some text", Importance.LOW,
                Date(), false, Date(), Date()
            )
        )
        tasksList.add(
            TodoItem(
                UUID.randomUUID().toString(), "Another text just to check that everything is ok... Blah-blah-blah-blah-blah-blah", Importance.MIDDLE,
                null, false, Date(), Date()
            )
        )
        tasksList.add(
            TodoItem(
                UUID.randomUUID().toString(), "A lot of data", Importance.LOW,
                Date(), true, Date(), Date()
            )
        )
        tasksList.add(
            TodoItem(
                UUID.randomUUID().toString(), "Do task", Importance.MIDDLE,
                Date(), true, Date(), Date()
            )
        )
        tasksList.add(
            TodoItem(
                UUID.randomUUID().toString(), "A\nN\nD\nR\nO\nI\nD", Importance.HIGH,
                Date(), true, Date(), Date()
            )
        )
        tasksList.add(
            TodoItem(
                UUID.randomUUID().toString(), "Do task323232323", Importance.HIGH,
                Date(), true, Date(), Date()
            )
        )
        tasksList.add(
            TodoItem(
                UUID.randomUUID().toString(), "Do task123123123", Importance.MIDDLE,
                Date(), false, Date(), Date()
            )
        )
        tasksList.add(
            TodoItem(
                UUID.randomUUID().toString(), "Do task10", Importance.MIDDLE,
                Date(), true, Date(), Date()
            )
        )

        tasks.value = tasksList
    }

    fun getPlainTasks(): List<TodoItem> {
        return tasks.value!!
    }

    public fun changeTaskDone(task: TodoItem, isDone: Boolean) {
        task.isDone = isDone
        if (isDone) {
            doneCounter.value = doneCounter.value?.plus(1)
        } else {
            doneCounter.value = doneCounter.value?.minus(1)
        }
    }

    public fun removeTask(taskId: String) {
        val task = (tasks.value as MutableList<TodoItem>).find { it.id.equals(taskId) }!!
        if (task.isDone) {
            doneCounter.value = doneCounter.value?.minus(1)
        }
        (tasks.value as MutableList<TodoItem>).remove(task)
    }

    public fun addTask(todoItem: TodoItem) {
        (tasks.value as MutableList<TodoItem>).add(todoItem)
    }

    public fun getTasks(): LiveData<List<TodoItem>> {
        return tasks
    }

    fun updateTask(
        taskId: String, taskText: String,
        deadline: Date?,
        importance: Importance
    ) {
        val task = (tasks.value as MutableList<TodoItem>).find { it.id.equals(taskId) } ?: return
        task.task = taskText
        task.deadline = deadline
        task.importance = importance
    }

    fun getTaskById(taskId: String): TodoItem {
        return (tasks.value as MutableList<TodoItem>).find { it.id.equals(taskId) }!!
    }

    fun getDoneCounter(): LiveData<Int> {
        return doneCounter
    }
}