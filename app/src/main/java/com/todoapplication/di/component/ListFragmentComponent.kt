package com.todoapplication.di.component

import com.todoapplication.view.fragments.TaskListFragment
import dagger.Subcomponent

@Subcomponent
interface ListFragmentComponent {
    fun inject(fragment: TaskListFragment)
}