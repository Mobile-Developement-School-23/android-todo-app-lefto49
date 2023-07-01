package com.todoapplication.view.activity

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.todoapplication.R
import com.todoapplication.data.entity.TodoItem
import com.todoapplication.util.background.NetworkChangeReceiver
import com.todoapplication.view.model.TaskViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var viewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.f_main)
        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        val rootLayout = findViewById<LinearLayout>(R.id.root)

        lifecycleScope.launch {
            viewModel.getSnackBar().collect {
                if (it != null) {
                    Snackbar.make(rootLayout, it, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        registerReceiver(
            NetworkChangeReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    fun changeTaskDone(task: TodoItem, isDone: Boolean) {
        task.isDone = isDone
        viewModel.updateTask(task)
    }
}