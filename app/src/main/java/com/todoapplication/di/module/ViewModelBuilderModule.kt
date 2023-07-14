package com.todoapplication.di.module

import androidx.lifecycle.ViewModelProvider
import com.todoapplication.di.annotation.ApplicationScope
import com.todoapplication.view.model.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface ViewModelBuilderModule {
    @ApplicationScope
    @Binds
    abstract fun bindViewModelFactory(
        factory: ViewModelFactory
    ): ViewModelProvider.Factory
}
