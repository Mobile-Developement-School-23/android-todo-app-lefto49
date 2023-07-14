package com.todoapplication.view.model

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.todoapplication.data.repository.TodoItemsRepository
import javax.inject.Inject

/**
 * Factory for creating the viewmodels.
 */
class ViewModelFactory @Inject constructor(
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
