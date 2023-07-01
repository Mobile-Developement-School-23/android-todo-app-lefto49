package com.todoapplication.view.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.todoapplication.data.entity.Importance
import com.todoapplication.R
import com.todoapplication.TodoApp
import com.todoapplication.data.entity.TodoItem
import com.todoapplication.view.activity.MainActivity
import com.todoapplication.view.model.TaskViewModel
import kotlinx.coroutines.launch
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TaskInfoFragment : Fragment() {
    private lateinit var saveButton: TextView
    private lateinit var cancelButton: ImageView
    private lateinit var deleteButton: TextView
    private lateinit var taskText: EditText
    private lateinit var importance: Spinner
    private lateinit var isDeadline: SwitchCompat
    private lateinit var deadline: TextView
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_task_fragment, container, false)
        val taskId = requireArguments().getString("taskId")!!
        val viewModel = ViewModelProvider(activity as MainActivity)[TaskViewModel::class.java]

        saveButton = view.findViewById(R.id.tv_save)
        cancelButton = view.findViewById(R.id.iv_cancel)
        deleteButton = view.findViewById(R.id.tv_delete)
        taskText = view.findViewById(R.id.et_task_text)
        importance = view.findViewById(R.id.sp_importance)
        isDeadline = view.findViewById(R.id.sw_deadline)
        deadline = view.findViewById(R.id.tv_date)

        var task = TodoItem("", "", Importance.basic, Date(), false, Date(), Date())
        lifecycleScope.launch {
            viewModel.getTaskById(taskId).collect {
                task = it
                taskText.setText(it.task)

                if (task.deadline != null) {
                    isDeadline.isChecked = true
                    deadline.visibility = View.VISIBLE
                    deadline.text = TodoApp.formatter.format(task.deadline)
                } else {
                    isDeadline.isChecked = false
                    deadline.visibility = View.INVISIBLE
                }

                importance.setSelection(task.importance.value)
            }
        }

        isDeadline.setOnClickListener {
            if (isDeadline.isChecked) {
                if (deadline.text.toString().isEmpty()) {
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

        deadline.setOnClickListener {
            val dialog = DatePickerDialog(
                activity as MainActivity,
                { _, year, month, day ->
                    deadline.text = String.format("%02d.%02d.%d", day, +1, year)
                },
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)
            )
            dialog.show()
        }

        saveButton.setOnClickListener {
            task.task = taskText.text.toString()
            if (isDeadline.isChecked) {
                task.deadline = TodoApp.formatter.parse(deadline.text.toString())
            } else {
                task.deadline = null
            }

            task.importance = listOf(
                Importance.basic,
                Importance.low,
                Importance.important
            )[importance.selectedItemPosition]
            task.editedAt = Date()
            viewModel.updateTask(task)
            findNavController().navigateUp()
        }

        deleteButton.setOnClickListener {
            viewModel.deleteTask(task)
            findNavController().navigateUp()
        }

        cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }

        val spinnerAdapter = ArrayAdapter(
            activity as MainActivity,
            android.R.layout.simple_spinner_item,
            listOf("Нет", "Низкая", "Высокая")
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        importance.adapter = spinnerAdapter
        importance.setSelection(task.importance.value)
        return view
    }
}