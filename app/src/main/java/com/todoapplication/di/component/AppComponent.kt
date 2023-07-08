package com.todoapplication.di.component

import android.content.Context
import com.todoapplication.TodoApp
import com.todoapplication.di.annotation.ApplicationScope
import com.todoapplication.di.module.*
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(
    modules = [DatabaseModule::class, NetworkModule::class, PreferencesModule::class, FormatterModule::class]
)
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
        ): AppComponent
    }

    fun inject(app: TodoApp)
    fun activityComponent(): ActivityComponent
}