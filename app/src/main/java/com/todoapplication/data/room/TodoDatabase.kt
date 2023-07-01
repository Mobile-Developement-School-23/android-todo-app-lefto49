package com.todoapplication.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.todoapplication.data.entity.TodoItem

@Database(
    entities = [TodoItem::class], version = 1
)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDAO
}