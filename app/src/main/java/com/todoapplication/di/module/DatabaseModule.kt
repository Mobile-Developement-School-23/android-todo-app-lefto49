package com.todoapplication.di.module

import android.content.Context
import androidx.room.Room
import com.todoapplication.data.room.TodoDatabase
import com.todoapplication.di.annotation.ApplicationScope
import dagger.Module
import dagger.Provides

@Module
interface DatabaseModule {
    companion object {
        @ApplicationScope
        @Provides
        fun db(context: Context): TodoDatabase =
            Room.databaseBuilder(context, TodoDatabase::class.java, "db").build()

        @ApplicationScope
        @Provides
        fun todoDao(db: TodoDatabase) = db.todoDao()
    }
}