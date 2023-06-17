package com.todoapplication.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.todoapplication.R
import com.todoapplication.util.TaskAdapter
import com.todoapplication.util.TasksDiffUtil
import com.todoapplication.TodoApp

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TaskListFragment : Fragment() {
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var addTask: FloatingActionButton
    private lateinit var counterDone: TextView
    private lateinit var invisible: ImageView
    private lateinit var visible: ImageView
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.task_list_fragment, container, false)

        tasksRecyclerView = view.findViewById(R.id.rv_tasks)
        addTask = view.findViewById(R.id.fb_add_task)
        counterDone = view.findViewById(R.id.tv_done_counter)
        invisible = view.findViewById(R.id.iv_invisible)
        visible = view.findViewById(R.id.iv_visible)

        TodoApp.repo.getDoneCounter().observe(viewLifecycleOwner) { value ->
            if (value == null) {
                return@observe
            }
            counterDone.text = value.toString()
        }

        tasksRecyclerView.layoutManager = LinearLayoutManager(activity)
        adapter =
            TaskAdapter(TodoApp.repo.getPlainTasks(), (activity as MainActivity))

        tasksRecyclerView.adapter = adapter
        TodoApp.repo.getTasks().observe(viewLifecycleOwner) { tasks ->
            if (tasks == null) {
                return@observe
            }
            val diffCallback = TasksDiffUtil(adapter.getData(), tasks)
            val result = DiffUtil.calculateDiff(diffCallback)
            result.dispatchUpdatesTo(adapter)
        }

        invisible.setOnClickListener {
            invisible.visibility = View.GONE
            invisible.isClickable = false

            visible.visibility = View.VISIBLE
            visible.isClickable = true
            val diffCallback =
                TasksDiffUtil(adapter.getData(), TodoApp.repo.getPlainTasks().filter { !(it.isDone) })
            val result = DiffUtil.calculateDiff(diffCallback)
            adapter.updateData(TodoApp.repo.getPlainTasks().filter { !(it.isDone) })
            result.dispatchUpdatesTo(adapter)
        }

        visible.setOnClickListener {
            visible.visibility = View.GONE
            visible.isClickable = false

            invisible.visibility = View.VISIBLE
            invisible.isClickable = true
            val diffCallback = TasksDiffUtil(adapter.getData(), TodoApp.repo.getPlainTasks())
            val result = DiffUtil.calculateDiff(diffCallback)
            adapter.updateData(TodoApp.repo.getPlainTasks())
            result.dispatchUpdatesTo(adapter)
        }

        addTask.setOnClickListener {
            (activity as MainActivity).showAddTaskFragment()
        }
        return view
    }
}