package com.todoapplication.view.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.todoapplication.data.entity.Importance
import com.todoapplication.R
import com.todoapplication.TodoApp
import com.todoapplication.data.entity.TodoItem
import com.todoapplication.view.activity.MainActivity
import com.todoapplication.view.model.TaskViewModel
import com.todoapplication.view.model.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * A fragment for adding the task.
 */
class AddTaskFragment : Fragment() {
    private lateinit var saveButton: TextView
    private lateinit var cancelButton: ImageView
    private lateinit var deleteButton: TextView
    private lateinit var taskText: EditText
    private lateinit var importance: TextView
    private lateinit var isDeadline: SwitchCompat
    private lateinit var deadline: TextView
    private lateinit var deleteIcon: ImageView
    private lateinit var navController: NavController
    private lateinit var task: TodoItem
    private lateinit var editMode: Boolean
    private val calendar = Calendar.getInstance()

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
        val view = inflater.inflate(R.layout.add_task_fragment, container, false)
        task = TodoItem("", "task", Importance.basic, Date(), true, Date(), Date())
        navController = findNavController()
        viewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]
/*
        setView(view)

        isDeadline.setOnClickListener {
            switchListener()
            resources
        }

        deadline.setOnClickListener {
            deadlineListener()
        }

        importance.setOnClickListener {
            BottomSheetConfig.setBottomSheet(activity as MainActivity, importance, resources)
        }

        saveButton.setOnClickListener {
            saveButtonListener()
        }

        cancelButton.setOnClickListener {
            navController.navigateUp()
        }
        return view*/
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                setLayout()
            }
        }
    }

    private fun setView(view: View) {
        navController = findNavController()
        viewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]

        saveButton = view.findViewById(R.id.tv_save)
        cancelButton = view.findViewById(R.id.iv_cancel)
        deleteButton = view.findViewById(R.id.tv_delete)
        taskText = view.findViewById(R.id.et_task_text)
        importance = view.findViewById(R.id.tv_importance_choose)
        isDeadline = view.findViewById(R.id.sw_deadline)
        deadline = view.findViewById(R.id.tv_date)
        deleteIcon = view.findViewById(R.id.iv_delete)

        deleteButton.visibility = View.GONE
        deleteIcon.visibility = View.GONE
    }

    private fun switchListener() {
        if (isDeadline.isChecked) {
            if (deadline.text.toString().isEmpty()) {
                val dialog = DatePickerDialog(
                    activity as MainActivity,
                    { _, year, month, day ->
                        deadline.text = String.format("%02d.%02d.%d", day, month + 1, year)
                    },
                    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR)
                )
                dialog.show()
                dialog.setOnCancelListener {
                    isDeadline.isChecked = false
                }
            }
            deadline.visibility = View.VISIBLE
            deadline.isClickable = true
        } else {
            deadline.visibility = View.GONE
            deadline.isClickable = false
        }
    }

    private fun saveButtonListener() {
        val createdAt = Date()

        val importance = when (importance.text.toString()) {
            resources.getString(R.string.low) -> Importance.low
            resources.getString(R.string.high) -> Importance.important
            else -> Importance.basic
        }

        val task = TodoItem(
            UUID.randomUUID().toString(),
            taskText.text.toString(),
            importance,
            null, false, createdAt, createdAt
        )

        if (isDeadline.isChecked) {
            task.deadline = formatter.parse(deadline.text.toString())
        }

        viewModel.addTask(task)
        navController.navigateUp()
    }

    private fun deadlineListener() {
        val dialog = DatePickerDialog(
            activity as MainActivity,
            { _, year, month, day ->
                deadline.text = String.format("%02d.%02d.%d", day, month + 1, year)
            },
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR)
        )
        dialog.show()
    }

    @Preview
    @Composable
    private fun setLayout() {
        val textBody = TextStyle(fontSize = 16.sp, lineHeight = 20.sp, color = Color.Black)
        val textButton = TextStyle(fontSize = 14.sp, lineHeight = 24.sp, color = Color.Blue)
        val textDelete = TextStyle(fontSize = 16.sp, lineHeight = 20.sp, color = Color.Red)

        Column(Modifier.padding(horizontal = 20.dp, vertical = 15.dp)) {
            Row {
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Close, null)
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = resources.getString(R.string.save).uppercase(),
                    style = textButton
                )
            }

            Column(
                modifier = Modifier.padding(
                    vertical = 10.dp,
                    horizontal = 0.dp
                )
            ) {
                TextField(
                    value = task.task,
                    onValueChange = { task.task = it },
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
                Text("High", style = textBody)
                Row(modifier = Modifier.padding(vertical = 32.dp)) {
                    Column {
                        Text(resources.getString(R.string.do_until), style = textBody)
                        Text("Date", style = textBody)
                    }
                    Spacer(Modifier.weight(1f))
                    Switch(checked = task.deadline != null, onCheckedChange = {
                        //TODO()
                    })
                }
                if (false) {
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
                            //TODO()
                        }) {
                            Text(resources.getString(R.string.delete), style = textDelete)
                        }
                    }
                }
            }
        }
    }
}