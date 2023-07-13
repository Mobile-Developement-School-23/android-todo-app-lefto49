package com.todoapplication.view.activity

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.SnackbarDuration
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.todoapplication.R
import com.todoapplication.TodoApp
import com.todoapplication.data.entity.TodoItem
import com.todoapplication.util.background.NetworkChangeReceiver
import com.todoapplication.view.model.TaskViewModel
import com.todoapplication.view.model.ViewModelFactory
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A main activity of the application.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var rootLayout: LinearLayout

    @Inject
    lateinit var receiver: NetworkChangeReceiver

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as TodoApp).appComponent.activityComponent().inject(this)
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]
        actionBar?.hide()
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.f_main)
        rootLayout = findViewById<LinearLayout>(R.id.root)

        lifecycleScope.launch {
            viewModel.getSnackBar().flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect {
                if (it != null) {
                    Snackbar.make(rootLayout, it, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        registerReceiver(
            receiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    fun changeTaskDone(task: TodoItem, isDone: Boolean) {
        task.isDone = isDone
        viewModel.updateTask(task)
    }

    fun deleteTask(task: TodoItem) {
        val snackBar = Snackbar.make(rootLayout, "Text", Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction("Отмена") {
            snackBar.dismiss()
        }
        snackBar.show()
        object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                snackBar.setText("Удалить ${task.task} ${millisUntilFinished / 1000 + 1}")
            }

            override fun onFinish() {
                if (snackBar.isShown) {
                    viewModel.deleteTask(task)
                    snackBar.dismiss()
                }
            }
        }.start()
    }
}