package com.todoapplication.di.module

import android.app.Activity
import android.widget.ArrayAdapter
import com.todoapplication.view.activity.MainActivity
import dagger.Module
import dagger.Provides
import java.text.SimpleDateFormat

@Module
interface AddFragmentLayoutModule {
    companion object {
        @Provides
        fun spinnerAdapter(activity: Activity): ArrayAdapter<String> {
            val spinnerAdapter = ArrayAdapter(
                activity as MainActivity,
                android.R.layout.simple_spinner_item,
                listOf("Нет", "Низкая", "Высокая")
            )
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            return spinnerAdapter
        }
    }
}