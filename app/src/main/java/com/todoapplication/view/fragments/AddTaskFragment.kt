package com.todoapplication.view.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.todoapplication.data.entity.Importance
import com.todoapplication.R
import com.todoapplication.TodoApp
import com.todoapplication.data.entity.TodoItem
import com.todoapplication.data.repository.TodoItemsRepository
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
    private lateinit var importance: Spinner
    private lateinit var isDeadline: SwitchCompat
    private lateinit var deadline: TextView
    private lateinit var deleteIcon: ImageView
    private lateinit var navController: NavController
    private val calendar = Calendar.getInstance()

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
        val view = inflater.inflate(R.layout.add_task_fragment, container, false)
        viewModelFactory = ViewModelFactory(repo, preferences)

        setView(view)

        isDeadline.setOnClickListener {
            switchListener()
        }

        deadline.setOnClickListener {
            deadlineListener()
        }

        saveButton.setOnClickListener {
            saveButtonListener()
        }

        cancelButton.setOnClickListener {
            navController.navigateUp()
        }
        return view
    }

    private fun setView(view: View) {
        navController = findNavController()
        viewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]

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

        val spinnerAdapter = ArrayAdapter(activity as MainActivity, android.R.layout.simple_spinner_item,
            listOf("Нет", "Низкая", "Высокая")
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        importance.adapter = spinnerAdapter
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
}