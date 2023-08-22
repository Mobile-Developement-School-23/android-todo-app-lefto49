package com.todoapplication.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.todoapplication.R
import com.todoapplication.data.entity.Importance
import com.todoapplication.data.entity.TodoItem
import com.todoapplication.view.activity.MainActivity
import java.text.SimpleDateFormat

/**
 * Adapter for displaying each task in a RecyclerView.
 */
class TaskAdapter(
    var tasks: List<TodoItem>,
    var activity: Context,
    var listener: OnTaskListener,
    val formatter: SimpleDateFormat
) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    interface OnTaskListener {
        fun onClick(taskId: String)
    }

    inner class TaskViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val isDoneCheckbox = itemView.findViewById<CheckBox>(R.id.cb_task)
        private val deadlineText = itemView.findViewById<TextView>(R.id.tv_deadline)
        private val taskText = itemView.findViewById<TextView>(R.id.tv_task)
        private lateinit var taskItem: TodoItem

        fun onBind(task: TodoItem) {
            taskItem = task
            isDoneCheckbox.isChecked = false
            taskText.paintFlags =
                (taskText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG) xor Paint.STRIKE_THRU_TEXT_FLAG

            setTaskData()

            isDoneCheckbox.setOnClickListener {
                isDoneClickListener()
            }

            itemView.setOnClickListener {
                listener.onClick(task.id)
            }
        }

        private fun setTaskData() {
            when (taskItem.importance) {
                Importance.low -> taskText.text = String.format("↓ %s", taskItem.task)
                Importance.important -> taskText.text = String.format("!! %s", taskItem.task)
                else -> taskText.text = taskItem.task
            }

            if (taskItem.deadline != null) {
                deadlineText.visibility = View.VISIBLE
                deadlineText.text = formatter.format(taskItem.deadline)
                isDoneCheckbox.buttonTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.red))
            } else {
                deadlineText.visibility = View.INVISIBLE
                isDoneCheckbox.buttonTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        activity,
                        R.color.colorOnSecondary
                    )
                )
            }

            if (taskItem.isDone) {
                isDoneCheckbox.isChecked = true

                isDoneCheckbox.buttonTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        activity,
                        R.color.green
                    )
                )
                taskText.paintFlags = taskText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        }

        private fun isDoneClickListener() {
            (activity as MainActivity).changeTaskDone(taskItem, isDoneCheckbox.isChecked)

            if (taskItem.deadline != null) {
                isDoneCheckbox.buttonTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        activity,
                        R.color.red
                    )
                )
            } else {
                isDoneCheckbox.buttonTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        activity,
                        R.color.colorOnSecondary
                    )
                )
            }

            if (isDoneCheckbox.isChecked) {
                isDoneCheckbox.buttonTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        activity,
                        R.color.green
                    )
                )
                taskText.paintFlags = taskText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                taskText.paintFlags = taskText.paintFlags xor Paint.STRIKE_THRU_TEXT_FLAG
            }
        }

        fun removeItem() {
            (activity as MainActivity).deleteTask(taskItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.task_viewholder, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.onBind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size

    fun updateData(newTasks: List<TodoItem>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}