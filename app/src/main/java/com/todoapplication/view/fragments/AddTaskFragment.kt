package com.todoapplication.view.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.todoapplication.data.entity.Importance
import com.todoapplication.R
import com.todoapplication.TodoApp
import com.todoapplication.data.entity.TodoItem
import com.todoapplication.view.activity.MainActivity
import com.todoapplication.view.model.TaskViewModel
import java.util.*

class AddTaskFragment : Fragment() {
    private lateinit var saveButton: TextView
    private lateinit var cancelButton: ImageView
    private lateinit var deleteButton: TextView
    private lateinit var taskText: EditText
    private lateinit var importance: Spinner
    private lateinit var isDeadline: SwitchCompat
    private lateinit var deadline: TextView
    private lateinit var deleteIcon: ImageView
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_task_fragment, container, false)
        val viewModel = ViewModelProvider(activity as MainActivity)[TaskViewModel::class.java]
        val navController = findNavController()

        saveButton = view.findViewById(R.id.tv_save)
        cancelButton = view.findViewById(R.id.iv_cancel)
        deleteButton = view.findViewById(R.id.tv_delete)
        taskText = view.findViewById(R.id.et_task_text)
        importance = view.findViewById(R.id.sp_importance)
        isDeadline = view.findViewById(R.id.sw_deadline)
        deadline = view.findViewById(R.id.tv_date)
        deleteIcon = view.findViewById(R.id.iv_delete)

        deleteButton.visibility = View.GONE
        deleteIcon.visibility = View.GONE

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
                    deadline.text = String.format("%02d.%02d.%d", day, month + 1, year)
                },
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)
            )
            dialog.show()
        }

        saveButton.setOnClickListener {
            val createdAt = Date()
            val task = TodoItem(
                UUID.randomUUID().toString(),
                taskText.text.toString(),
                listOf(
                    Importance.basic,
                    Importance.low,
                    Importance.important
                )[importance.selectedItemPosition],
                null, false, createdAt, createdAt
            )

            if (isDeadline.isChecked) {
                task.deadline = TodoApp.formatter.parse(deadline.text.toString())
            }

            viewModel.addTask(task)
            navController.navigateUp()
        }

        cancelButton.setOnClickListener {
            navController.navigateUp()
        }

        val spinnerAdapter = ArrayAdapter(
            activity as MainActivity,
            android.R.layout.simple_spinner_item,
            listOf("Нет", "Низкая", "Высокая")
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        importance.adapter = spinnerAdapter
        return view
    }
}