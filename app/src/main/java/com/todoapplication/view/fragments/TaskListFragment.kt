package com.todoapplication.view.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.todoapplication.R
import com.todoapplication.TodoApp
import com.todoapplication.data.repository.TodoItemsRepository
import com.todoapplication.util.SwipeHandler
import com.todoapplication.util.TaskAdapter
import com.todoapplication.view.activity.MainActivity
import com.todoapplication.view.model.TaskViewModel
import com.todoapplication.view.model.ViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import javax.inject.Inject

/**
 * Fragment for displaying a list of tasks to the user.
 */
class TaskListFragment : Fragment(), TaskAdapter.OnTaskListener {
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var addTask: FloatingActionButton
    private lateinit var counterDone: TextView
    private lateinit var visibility: CheckBox
    private lateinit var adapter: TaskAdapter

    //private lateinit var refresh: SwipeRefreshLayout
    private lateinit var themeSettings: ImageView

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
        (requireActivity().application as TodoApp).appComponent.activityComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.task_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView(view)
        setDataUpdates()

        visibility.setOnClickListener {
            if (!visibility.isChecked) {
                adapter.updateData(viewModel.getTasksList().filter { !(it.isDone) })
            } else {
                adapter.updateData(viewModel.getTasksList())
            }
        }

        /*        refresh.setOnRefreshListener {
                    viewModel.uploadData()
                }*/

        themeSettings.setOnClickListener {
            val act = activity as MainActivity
            BottomSheetConfig.setBottomSheet(act)
        }

        addTask.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("editMode", false)
            findNavController().navigate(R.id.action_taskListFragment_to_addTaskFragment, bundle)
        }
    }

    private fun setUpView(view: View) {
        viewModelFactory = ViewModelFactory(repo, preferences)
        viewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]

        tasksRecyclerView = view.findViewById(R.id.rv_tasks)
        addTask = view.findViewById(R.id.fb_add_task)
        counterDone = view.findViewById(R.id.tv_done_counter)
        visibility = view.findViewById(R.id.cb_visibility)
        // refresh = view.findViewById(R.id.swiperefresh)
        themeSettings = view.findViewById(R.id.iv_theme)
        tasksRecyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = TaskAdapter(listOf(), (activity as MainActivity), this, formatter)

        tasksRecyclerView.adapter = adapter

        ItemTouchHelper(SwipeHandler()).attachToRecyclerView(tasksRecyclerView)
        tasksRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    private fun setDataUpdates() {
        lifecycleScope.launch {
            viewModel.getTasks()
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED).collect {
                    //refresh.isRefreshing = false
                    var newTasks = it
                    if (!visibility.isChecked) {
                        newTasks = it.filter { item -> !item.isDone }
                    }

                    adapter.updateData(newTasks)
                    counterDone.text =
                        resources.getString(R.string.done, it.count { item -> item.isDone })
                }
        }
    }

    override fun onClick(taskId: String) {
        val bundle = Bundle()
        bundle.putString("taskId", taskId)
        bundle.putBoolean("editMode", true)
        findNavController().navigate(R.id.action_taskListFragment_to_addTaskFragment, bundle)
    }
}