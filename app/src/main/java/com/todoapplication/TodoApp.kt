package com.todoapplication

import android.app.Application
import com.todoapplication.data.TodoItemsRepository
import java.text.SimpleDateFormat

class TodoApp: Application() {
    companion object{
        public val repo = TodoItemsRepository()
        public val formatter = SimpleDateFormat("dd.MM.yyyy")
    }
}