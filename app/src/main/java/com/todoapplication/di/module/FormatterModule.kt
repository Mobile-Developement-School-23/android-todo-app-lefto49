package com.todoapplication.di.module

import dagger.Module
import dagger.Provides
import java.text.SimpleDateFormat

@Module
interface FormatterModule {
    companion object {
        @Provides
        fun formatter() = SimpleDateFormat("dd.MM.yyyy HH:mm")
    }
}