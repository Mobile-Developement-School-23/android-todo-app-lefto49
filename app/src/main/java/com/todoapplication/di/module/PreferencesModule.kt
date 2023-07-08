package com.todoapplication.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
interface PreferencesModule {
    companion object {
        @Provides
        fun preferences(context: Context) =
            context.getSharedPreferences("SharedPreferences", Application.MODE_PRIVATE)
    }
}