package com.todoapplication.di.component

import com.todoapplication.di.annotation.ActivityScope
import com.todoapplication.view.activity.MainActivity
import com.todoapplication.view.fragments.AddTaskFragment
import com.todoapplication.view.fragments.TaskInfoFragment
import com.todoapplication.view.fragments.TaskListFragment
import dagger.Subcomponent

@Subcomponent
interface ActivityComponent {
    fun inject(activity: MainActivity)
    fun inject(fragment: AddTaskFragment)
    fun inject(fragment: TaskInfoFragment)
    fun inject(fragment: TaskListFragment)
}