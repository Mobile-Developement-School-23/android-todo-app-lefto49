package com.todoapplication.view.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.todoapplication.data.repository.TodoItemsRepository
import com.todoapplication.view.AppTheme
import com.todoapplication.view.ExtendedTheme
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
    lateinit var repo: TodoItemsRepository

    @Inject
    lateinit var preferences: SharedPreferences

    private lateinit var viewModelFactory: ViewModelProvider.Factory
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

        viewModelFactory = ViewModelFactory(repo, preferences)
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

                AppTheme {
                    Layout()
                }
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
    @Composable
    private fun Layout() {
        val modalSheetState =
            rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
        val scope = rememberCoroutineScope()

        ModalBottomSheetLayout(sheetState = modalSheetState,
            modifier = Modifier.background(MaterialTheme.colors.primary),
            sheetContent = {
                SheetBlock()
            }) {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 15.dp)) {
                UpperBar()

                Column(
                    modifier = Modifier.padding(
                        vertical = 10.dp,
                        horizontal = 0.dp
                    )
                ) {
                    MainBlock()

                    TextButton(onClick = { scope.launch { modalSheetState.show() } }) {
                        Text(
                            importanceText.value, style = ExtendedTheme.typography.body,
                            color = MaterialTheme.colors.onPrimary
                        )
                    }

                    DeadlineBlock()

                    if (editMode) {
                        DeleteBlock()
                    }
                }
            }
        }
    }

    @Composable
    private fun DeleteBlock() {
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
                        tint = ExtendedTheme.colors.red,
                        modifier = Modifier.padding(vertical = 0.dp)
                    )
                }
            }
            TextButton(onClick = {
                (activity as MainActivity).deleteTask(task)
                navController.navigateUp()
            }) {
                Text(
                    resources.getString(R.string.delete),
                    style = ExtendedTheme.typography.body,
                    color = ExtendedTheme.colors.red
                )
            }
        }
    }

    @Composable
    private fun UpperBar() {
        Row {
            IconButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    null,
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            Spacer(Modifier.weight(1f))
            TextButton(
                onClick = { saveTask() }
            ) {
                Text(
                    resources.getString(R.string.save).uppercase(),
                    style = ExtendedTheme.typography.button,
                    color = ExtendedTheme.colors.colorAccent
                )
            }
        }
    }

    @Composable
    private fun DeadlineBlock() {
        Row(modifier = Modifier.padding(vertical = 32.dp)) {
            Column {
                Text(
                    resources.getString(R.string.do_until),
                    style = ExtendedTheme.typography.body,
                    color = MaterialTheme.colors.onPrimary
                )

                TextButton(onClick = { showDialog() }) {
                    Text(
                        setDate(task.deadline), style = ExtendedTheme.typography.body,
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Switch(
                checked = deadlineText.value != "",
                onCheckedChange = {
                    chooseDeadline(it)
                },
            )
        }
    }

    @Composable
    private fun SheetBlock() {
        val importances = listOf(
            resources.getString(R.string.no),
            resources.getString(R.string.low),
            resources.getString(R.string.high)
        )

        Column {
            Text(
                "Выберите важность",
                style = ExtendedTheme.typography.body,
                color = MaterialTheme.colors.onPrimary
            )

            importances.forEach {
                Row {
                    RadioButton(
                        selected = importanceText.value == it,
                        onClick = { updateImportance(it) })
                    Text(
                        it,
                        style = ExtendedTheme.typography.body,
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    }

    @Composable
    private fun MainBlock() {
        TextField(
            value = taskText.value,
            onValueChange = { taskText.value = it },
            textStyle = ExtendedTheme.typography.body,
            placeholder = { Text(resources.getString(R.string.to_do)) },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.secondary),
            readOnly = false,
            minLines = 6,
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colors.onSecondary,
                backgroundColor = MaterialTheme.colors.secondary
            )
        )
        Spacer(modifier = Modifier.padding(vertical = 16.dp))

        Text(
            text = resources.getString(R.string.importance),
            style = ExtendedTheme.typography.body,
            color = MaterialTheme.colors.onPrimary
        )
    }

    @Preview
    @Composable
    private fun LayoutPreview() {
        Layout()
    }
}
