package com.todoapplication.di.component

import com.todoapplication.di.annotation.FragmentScope
import com.todoapplication.view.fragments.AddTaskFragment
import com.todoapplication.view.fragments.TaskInfoFragment
import com.todoapplication.view.fragments.TaskListFragment
import dagger.Subcomponent

@Subcomponent
interface FragmentComponent {
    fun inject(fragment: AddTaskFragment)
    fun inject(fragment: TaskInfoFragment)
    fun inject(fragment: TaskListFragment)
}