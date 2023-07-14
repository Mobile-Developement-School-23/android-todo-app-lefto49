package com.todoapplication.di.module

import androidx.lifecycle.ViewModel
import com.todoapplication.di.annotation.ViewModelKey
import com.todoapplication.view.model.TaskViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(TaskViewModel::class)
    fun bindViewModel(viewModel: TaskViewModel): ViewModel
}