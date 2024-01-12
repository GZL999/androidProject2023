package com.gzl.todo.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gzl.todo.R
import com.gzl.todo.data.Api
import com.gzl.todo.data.TasksListViewModel
import com.gzl.todo.detail.DetailActivity
import com.gzl.todo.user.UserActivity
import kotlinx.coroutines.launch

class TaskListFragment<RecyclerView : View?> : Fragment() {

    private val viewModel: TasksListViewModel by viewModels()

    private var taskList = mutableListOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2"),
        Task(id = "id_3", title = "Task 3")
    )

    private val createTaskLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as Task?
        if (task != null){
            viewModel.add(task)
        }
    }

    private val editTaskLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as Task?
        if (task != null){
            viewModel.edit(task)
        }
    }

    val adapterListener : TaskListListener = object : TaskListListener {
        override fun onClickDelete(task: Task) {
            viewModel.remove(task)
        }

        override fun onClickEdit(task: Task) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("task", task)
            editTaskLauncher.launch(intent)
        }

        override fun onLongClickListener(task: Task) : Boolean {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Titre: "+ task.title+ "\nDescription: "+ task.description)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
            return true
        }
    }

    private val adapter = TaskListAdapter(adapterListener)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.gzl)
        val floatingActionButton = view.findViewById<FloatingActionButton>(R.id.floatingActionButton2)
        val imageAvatarButton = view.findViewById<ImageView>(R.id.imageAvatar)

        val intentDetail = Intent(context, DetailActivity::class.java)
        val intentUser = Intent(context, UserActivity::class.java)
        floatingActionButton.setOnClickListener{
            createTaskLauncher.launch(intentDetail)
        }

        imageAvatarButton.setOnClickListener{

            startActivity(intentUser)
        }

        //var sizeTaskList = savedInstanceState?.getSerializable("nbTask")//.toString().toInt()
        val sizeTaskList = savedInstanceState?.getSerializable("tasklist") as? Array<Task>

        taskList = (sizeTaskList?.toList() ?: emptyList()).toMutableList()

        recyclerView.adapter = adapter
        adapter.submitList(taskList)

        lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            viewModel.tasksStateFlow.collect { newList ->
                // cette lambda est exécutée à chaque fois que la liste est mise à jour dans le VM
                // -> ici, on met à jour la liste dans l'adapter
                adapter.submitList(newList)
            }
        }

        viewModel.refresh()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("tasklist",taskList.toTypedArray())
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            // Ici on ne va pas gérer les cas d'erreur donc on force le crash avec "!!"
            val user = Api.userWebService.fetchUser().body()!!
            val userTextView = view?.findViewById<TextView>(R.id.userTextView)
            val userImageAvatar = view?.findViewById<ImageView>(R.id.imageAvatar)
            if (userTextView != null) {
                userTextView.text = user.name
            }

            if(userImageAvatar != null){
                userImageAvatar.load(user.avatar) {
                    error(R.drawable.ic_launcher_background) // image par défaut en cas d'erreur
                }
            }
            userImageAvatar?.load(user.avatar) {
                error(R.drawable.ic_launcher_background) // image par défaut en cas d'erreur
            }
        }

        viewModel.refresh()
    }
}
