package com.todoapplication.data.room

import androidx.room.*
import com.todoapplication.data.entity.TodoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDAO {
    @Query("SELECT * FROM todoitems")
    fun getAll(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todoitems")
    fun getAllList(): List<TodoItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(tasks: List<TodoItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTask(task: TodoItem)

    @Query("DELETE FROM todoitems")
    fun deleteAll()

    @Delete
    fun deleteTask(task: TodoItem)

    @Transaction
    fun uploadInitialData(tasks: List<TodoItem>) {
        deleteAll()
        addAll(tasks)
    }

    @Query("SELECT * FROM todoitems WHERE id = :taskId")
    fun getTaskById(taskId: String): Flow<TodoItem>
}
