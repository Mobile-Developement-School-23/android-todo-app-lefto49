package com.todoapplication.view.fragments

import android.app.DatePickerDialog
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.todoapplication.data.entity.Importance
import com.todoapplication.R
import com.todoapplication.TodoApp
import com.todoapplication.data.entity.TodoItem
import com.todoapplication.view.activity.MainActivity
import com.todoapplication.view.model.TaskViewModel
import com.todoapplication.view.model.ViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * A fragment for adding the task.
 */
class AddTaskFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var task: TodoItem

    private var editMode = false
    private val calendar = Calendar.getInstance()
    private lateinit var taskText: MutableState<String>
    private lateinit var deadlineText: MutableState<String>
    private lateinit var importanceText: MutableState<String>

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: TaskViewModel

    @Inject
    lateinit var formatter: SimpleDateFormat

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as TodoApp).appComponent.activityComponent()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        editMode = requireArguments().getBoolean("editMode")
        task = TodoItem("", "", Importance.basic, null, false, Date(), Date())
        navController = findNavController()
        viewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]

        val view = ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                taskText = remember { mutableStateOf("") }
                deadlineText = remember { mutableStateOf("") }
                importanceText = remember { mutableStateOf(resources.getString(R.string.no)) }

                if (editMode) {
                    setDataUpload()
                }

                setLayout()
            }
        }

        return view
    }

    private fun setDataUpload() {
        val taskId = requireArguments().getString("taskId")!!

        lifecycleScope.launch {
            viewModel.getTaskById(taskId).flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.STARTED
            ).collect {
                task = it
                taskText.value = it.task

                if (it.deadline != null) {
                    deadlineText.value = formatter.format(it.deadline)
                }
                importanceText.value = setImportance(it.importance)
            }
        }
    }

    private fun saveTask() {
        if (editMode) {
            task.editedAt = Date()
            task.task = taskText.value
            (activity as MainActivity).removeNotification(task.id)

            if (task.deadline != null) {
                (activity as MainActivity).createNotification(task)
            }
            viewModel.updateTask(task)
        } else {
            val createdAt = Date()
            task.createdAt = createdAt
            task.editedAt = createdAt
            task.id = UUID.randomUUID().toString()
            task.task = taskText.value

            if (task.deadline != null) {
                (activity as MainActivity).createNotification(task)
            }
            viewModel.addTask(task)
        }
        navController.navigateUp()
    }

    private fun setImportance(importance: Importance): String = when (importance) {
        Importance.low -> resources.getString(R.string.low)
        Importance.important -> resources.getString(R.string.high)
        else -> resources.getString(R.string.no)
    }

    private fun chooseDeadline(checked: Boolean) {
        if (checked) {
            showDialog()
        } else {
            task.deadline = null
            deadlineText.value = ""
        }
    }

    private fun showDialog() {
        val dialog = DatePickerDialog(
            activity as MainActivity,
            { _, _, _, _ ->
                TimePickerDialog(
                    activity,

                    { _, hour, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendar.set(Calendar.MINUTE, minute)
                        task.deadline = calendar.time
                        deadlineText.value = formatter.format(task.deadline)
                    },
                    12,
                    0,
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.datePicker.minDate = calendar.timeInMillis
        dialog.show()
    }

    private fun setDate(date: Date?): String {
        if (date == null) {
            return ""
        }

        return formatter.format(date)
    }

    private fun updateImportance(importance: String) {
        importanceText.value = importance

        task.importance = when (importance) {
            resources.getString(R.string.low) -> Importance.low
            resources.getString(R.string.high) -> Importance.important
            else -> Importance.basic
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Preview
    @Composable
    private fun setLayout() {
        val textBody = TextStyle(fontSize = 16.sp, lineHeight = 20.sp, color = Color.Black)
        val textButton = TextStyle(fontSize = 14.sp, lineHeight = 24.sp, color = Color.Blue)
        val textDelete = TextStyle(fontSize = 16.sp, lineHeight = 20.sp, color = Color.Red)

        val modalSheetState =
            rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden) 
        val scope = rememberCoroutineScope()

        val importances = listOf(
            resources.getString(R.string.no),
            resources.getString(R.string.low),
            resources.getString(R.string.high)
        )

        ModalBottomSheetLayout(sheetState = modalSheetState, sheetContent = {
            Column {
                Text("Выберите важность", style = textBody)

                importances.forEach {
                    Row {
                        RadioButton(
                            selected = importanceText.value == it,
                            onClick = { updateImportance(it) })
                        Text(it, style = textBody)
                    }
                }
            }
        }) {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 15.dp)) {
                Row {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, null)
                    }
                    Spacer(Modifier.weight(1f))
                    TextButton(
                        onClick = { saveTask() }
                    ) {
                        Text(resources.getString(R.string.save).uppercase(), style = textButton)
                    }
                }

                Column(
                    modifier = Modifier.padding(
                        vertical = 10.dp,
                        horizontal = 0.dp
                    )
                ) {
                    TextField(
                        value = taskText.value,
                        onValueChange = { taskText.value = it },
                        textStyle = textBody,
                        placeholder = { Text(resources.getString(R.string.to_do)) },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = false,
                        minLines = 6
                    )
                    Spacer(modifier = Modifier.padding(vertical = 16.dp))

                    Text(
                        text = resources.getString(R.string.importance),
                        style = textBody
                    )
                    Spacer(modifier = Modifier.padding(vertical = 8.dp))

                    TextButton(onClick = { scope.launch { modalSheetState.show() } }) {
                        Text(importanceText.value, style = textBody)
                    }

                    Row(modifier = Modifier.padding(vertical = 32.dp)) {
                        Column {
                            Text(resources.getString(R.string.do_until), style = textBody)

                            TextButton(onClick = { showDialog() }) {
                                Text(setDate(task.deadline), style = textBody)
                            }
                        }
                        Spacer(Modifier.weight(1f))
                        Switch(checked = deadlineText.value != "", onCheckedChange = {
                            chooseDeadline(it)
                        })
                    }

                    if (editMode) {
                        Row(modifier = Modifier.padding(vertical = 32.dp)) {
                            Row(modifier = Modifier.padding(vertical = 12.dp)) {
                                IconButton(
                                    onClick = { navController.navigateUp() },
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(vertical = 0.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        null,
                                        tint = Color.Red,
                                        modifier = Modifier.padding(vertical = 0.dp)
                                    )
                                }
                            }
                            TextButton(onClick = {
                                (activity as MainActivity).deleteTask(task)
                                navController.navigateUp()
                            }) {
                                Text(resources.getString(R.string.delete), style = textDelete)
                            }
                        }
                    }
                }
            }
        }

    }
}