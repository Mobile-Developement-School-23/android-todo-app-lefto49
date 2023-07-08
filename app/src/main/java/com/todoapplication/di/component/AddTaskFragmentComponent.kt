package com.todoapplication.di.component

import android.app.Activity
import com.todoapplication.di.annotation.FragmentScope
import com.todoapplication.di.module.AddFragmentLayoutModule
import com.todoapplication.view.fragments.AddTaskFragment
import com.todoapplication.view.fragments.TaskInfoFragment
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(modules = [AddFragmentLayoutModule::class])
interface AddTaskFragmentComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance activity: Activity,
        ): AddTaskFragmentComponent
    }

    fun inject(fragment: AddTaskFragment)
    fun inject(fragment: TaskInfoFragment)
}