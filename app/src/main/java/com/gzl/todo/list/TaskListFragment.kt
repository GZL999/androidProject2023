package com.gzl.todo.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gzl.todo.R
import com.gzl.todo.databinding.FragmentTaskListBinding
import com.gzl.todo.detail.DetailActivity
import java.util.UUID

class TaskListFragment : Fragment() {
    private var taskList = mutableListOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2"),
        Task(id = "id_3", title = "Task 3")
    )

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val adapterListener = object : TaskListListener {
        override fun onClickDelete(task: Task) {
            taskList.remove(task)
            adapter.submitList(taskList.toList())
        }

        override fun onClickEdit(task: Task) {
            // Code to handle the edit action
            val editIntent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("task", task) // Assuming Task implements Serializable
            }
            editTaskLauncher.launch(editIntent)
        }
    }

    private val adapter:TaskListAdapter = TaskListAdapter(adapterListener)

    private val createTaskLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = result.data?.getSerializableExtra("task") as Task?
            task?.let {
                taskList.add(it)
                adapter.submitList(taskList.toList())
                adapter.notifyItemInserted(taskList.size-1)
            }
        }
    }

    private val editTaskLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val editedTask = result.data?.getSerializableExtra("task") as? Task
            editedTask?.let { task ->
                // Update the task in your list
                taskList = taskList.map { existingTask ->
                    if (existingTask.id == task.id) task else existingTask
                }.toMutableList()
                // Update the adapter
                adapter.submitList(taskList)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Assume 'taskList' is the list of tasks you want to save
        outState.putSerializable("task_list", ArrayList(taskList)) // ArrayList is Serializable
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Restore the task list if there is a saved state
        savedInstanceState?.getSerializable("task_list")?.let {
            val savedList = it as? ArrayList<*>
            // Update your task list and UI with 'savedList'
        }

        val recyclerView = binding.taskList
        recyclerView.adapter = adapter

        val floatingActionButton = binding.floatingActionButton
        floatingActionButton.setOnClickListener {

            val intent = Intent(context, DetailActivity::class.java)
            createTaskLauncher.launch(intent)
        }

        adapter.submitList(taskList.toList())
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}