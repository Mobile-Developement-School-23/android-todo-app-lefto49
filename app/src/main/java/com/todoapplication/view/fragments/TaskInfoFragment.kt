package com.todoapplication.view.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
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
 * A fragment for observing and editing a task.
 */
class TaskInfoFragment : Fragment() {
    private lateinit var saveButton: TextView
    private lateinit var cancelButton: ImageView
    private lateinit var deleteButton: TextView
    private lateinit var taskText: EditText
    private lateinit var importance: TextView
    private lateinit var isDeadline: SwitchCompat
    private lateinit var deadline: TextView
    private val calendar = Calendar.getInstance()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: TaskViewModel

    private lateinit var taskId: String
    private lateinit var task: TodoItem

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
        setView(view)
        updateData()

        isDeadline.setOnClickListener {
            deadlineSwitchListener()
        }

        deadline.setOnClickListener {
            deadlineClick()
        }

        importance.setOnClickListener {
            //BottomSheetConfig.setBottomSheet(activity as MainActivity, importance, resources)
        }

        saveButton.setOnClickListener {
            saveButtonClick()
        }
        deleteButton.setOnClickListener {
            viewModel.deleteTask(task)
            findNavController().navigateUp()
        }
        cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }

        return view
    }

    private fun setView(view: View) {
        taskId = requireArguments().getString("taskId")!!
        viewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]

        saveButton = view.findViewById(R.id.tv_save)
        cancelButton = view.findViewById(R.id.iv_cancel)
        deleteButton = view.findViewById(R.id.tv_delete)
        taskText = view.findViewById(R.id.et_task_text)
        importance = view.findViewById(R.id.tv_importance_choose)
        isDeadline = view.findViewById(R.id.sw_deadline)
        deadline = view.findViewById(R.id.tv_date)

        task = TodoItem("", "", Importance.basic, Date(), false, Date(), Date())
    }

    private fun updateData() {
        lifecycleScope.launch {
            viewModel.getTaskById(taskId).flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.STARTED
            ).collect {
                task = it
                taskText.setText(it.task)

                if (task.deadline != null) {
                    isDeadline.isChecked = true
                    deadline.visibility = View.VISIBLE
                    deadline.text = formatter.format(task.deadline)
                } else {
                    isDeadline.isChecked = false
                    deadline.visibility = View.INVISIBLE
                }

                importance.text = when (task.importance) {
                    Importance.low -> resources.getString(R.string.low)
                    Importance.important -> resources.getString(R.string.high)
                    else -> resources.getString(R.string.no)
                }
            }
        }
    }

    private fun deadlineSwitchListener() {
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

    private fun saveButtonClick() {
        task.task = taskText.text.toString()
        if (isDeadline.isChecked) {
            task.deadline = formatter.parse(deadline.text.toString())
        } else {
            task.deadline = null
        }

        task.importance = when (importance.text.toString()) {
            resources.getString(R.string.low) -> Importance.low
            resources.getString(R.string.high) -> Importance.important
            else -> Importance.basic
        }

        task.editedAt = Date()
        viewModel.updateTask(task)
        findNavController().navigateUp()
    }

    private fun deadlineClick() {
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