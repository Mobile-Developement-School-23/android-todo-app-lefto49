package com.todoapplication.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.todoapplication.R
import com.todoapplication.util.TaskAdapter
import com.todoapplication.view.activity.MainActivity
import com.todoapplication.view.model.TaskViewModel
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TaskListFragment : Fragment(), TaskAdapter.OnTaskListener {
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var addTask: FloatingActionButton
    private lateinit var counterDone: TextView
    private lateinit var invisible: ImageView
    private lateinit var visible: ImageView
    private lateinit var adapter: TaskAdapter
    private lateinit var refresh: SwipeRefreshLayout

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
        refresh = view.findViewById(R.id.swiperefresh)
        val viewModel = ViewModelProvider(activity as MainActivity)[TaskViewModel::class.java]

        tasksRecyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = TaskAdapter(listOf(), (activity as MainActivity), this)

        tasksRecyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.getTasks().collect {
                var newTasks = it
                if (invisible.visibility == View.GONE) {
                    newTasks = it.filter { item -> !item.isDone }
                }

                adapter.updateData(newTasks)
                counterDone.text = it.count { item -> item.isDone }.toString()
            }
        }

        invisible.setOnClickListener {
            invisible.visibility = View.GONE
            invisible.isClickable = false

            visible.visibility = View.VISIBLE
            visible.isClickable = true
            adapter.updateData(viewModel.getTasksList().filter { !(it.isDone) })
        }

        visible.setOnClickListener {
            visible.visibility = View.GONE
            visible.isClickable = false

            invisible.visibility = View.VISIBLE
            invisible.isClickable = true
            adapter.updateData(viewModel.getTasksList())
        }

        refresh.setOnRefreshListener {
            viewModel.uploadData()
            refresh.isRefreshing = false
        }

        addTask.setOnClickListener {
            findNavController().navigate(R.id.action_taskListFragment_to_addTaskFragment)
        }
        return view
    }

    override fun onClick(taskId: String) {
        val bundle = Bundle()
        bundle.putString("taskId", taskId)
        findNavController().navigate(R.id.action_taskListFragment_to_taskInfoFragment, bundle)
    }
}