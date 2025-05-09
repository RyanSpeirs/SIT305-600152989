package edu.deakin.s600152989.sit305.task41

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.deakin.s600152989.sit305.task41.TaskAdapter.OnTaskClickListener

class MainActivity : AppCompatActivity(), OnTaskClickListener {
    private var taskViewModel: TaskViewModel? = null
    private var taskAdapter: TaskAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.taskRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val nameList = ArrayList<Task>()
        nameList.add(Task("Alice", "Description for Alice", "01/05/2025 10:00", "02/05/2025 12:00"))
        nameList.add(Task("Bob", "Description for Bob", "01/05/2025 13:00", "03/05/2025 15:00"))
        nameList.add(
            Task(
                "Charlie",
                "Description for Charlie",
                "02/05/2025 09:00",
                "04/05/2025 11:00"
            )
        )


        taskAdapter = TaskAdapter(nameList, this)
        recyclerView.adapter = taskAdapter

        // Setup ViewModel
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        taskViewModel.getAllTasks().observe(this, object : Observer<List<Task?>?> {
            override fun onChanged(tasks: List<Task>) {
                // Update RecyclerView when task list changes
                taskAdapter!!.setTasks(tasks)
            }
        })

        // Observe LiveData to automatically update the RecyclerView
        taskViewModel.getAllTasks().observe(this, object : Observer<List<Task?>?> {
            override fun onChanged(tasks: List<Task>) {
                // This will be triggered whenever the list of tasks changes
                taskAdapter!!.setTasks(tasks) // Update the RecyclerView's adapter with new data
            }
        })

        // Add task FAB click listener
        val fab = findViewById<FloatingActionButton>(R.id.fab_add_task)
        fab.setOnClickListener { v: View? ->
            val intent = Intent(
                this@MainActivity,
                AddEditTaskActivity::class.java
            )
            startActivity(intent)
        }
    }

    // Handle the result from AddEditTaskActivity
    override fun onEditClick(task: Task) {
        // Pass task data to AddEditTaskActivity for editing
        val intent = Intent(
            this@MainActivity,
            AddEditTaskActivity::class.java
        )
        intent.putExtra("TASK_ID", task.id) // Pass the task ID to edit
        intent.putExtra("TASK_TITLE", task.title)
        intent.putExtra("TASK_DESCRIPTION", task.description)
        intent.putExtra("TASK_START_TIME", task.startDate)
        intent.putExtra("TASK_DUE_DATE", task.dueDate)

        startActivity(intent)
    }


    override fun onDeleteClick(task: Task?) {
        // Handle delete action
        AlertDialog.Builder(this@MainActivity)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Yes") { dialog: DialogInterface?, which: Int ->
                taskViewModel!!.delete(task) // Delete task from ViewModel
                Toast.makeText(this@MainActivity, "Task deleted", Toast.LENGTH_SHORT)
                    .show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show()
        }
    }
}
