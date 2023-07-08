package com.todoapplication.view.model

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.todoapplication.data.repository.TodoItemsRepository
import com.todoapplication.di.annotation.ApplicationScope
import javax.inject.Inject
import javax.inject.Provider

/**
 * Factory for creating the viewmodels.
 */
class ViewModelFactory(
    private val repo: TodoItemsRepository,
    private val preferences: SharedPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            TaskViewModel::class.java -> TaskViewModel(
                repo,
                preferences,
            ) as T
            else -> throw IllegalStateException("Unknown view model class")
        }
    }
}
