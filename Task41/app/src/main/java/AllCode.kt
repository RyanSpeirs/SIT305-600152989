package edu.deakin.s600152989.sit305.task41

import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.deakin.s600152989.sit305.task41.AddEditTaskActivity
import edu.deakin.s600152989.sit305.task41.R
import edu.deakin.s600152989.sit305.task41.Task
import edu.deakin.s600152989.sit305.task41.TaskAdapter
import edu.deakin.s600152989.sit305.task41.TaskAdapter.OnTaskClickListener
import edu.deakin.s600152989.sit305.task41.TaskDao
import edu.deakin.s600152989.sit305.task41.TaskDatabase
import edu.deakin.s600152989.sit305.task41.TaskViewModel
import java.util.Calendar
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AllCode {


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




    @Database(entities = [Task::class], version = 1, exportSchema = false) // No versioning needed
    abstract class TaskDatabase : RoomDatabase() {
        abstract fun taskDao(): TaskDao?

        companion object {
            // Executor for background database operations
            val databaseWriteExecutor: Executor = Executors.newFixedThreadPool(4)

            @Volatile
            private var instance: TaskDatabase? = null

            // Get instance of the database (Singleton pattern)
            @Synchronized
            fun getInstance(context: Context): TaskDatabase? {
                if (instance == null) {
                    synchronized(TaskDatabase::class.java) {
                        if (instance == null) {
                            instance = databaseBuilder(
                                context.applicationContext,
                                TaskDatabase::class.java,
                                "task_database"
                            )
                                .allowMainThreadQueries() // Allow main thread queries (if needed, for testing)
                                .build() // No migrations or fallbacks needed here
                        }
                    }
                }
                return instance
            }
        }
    }



    @Dao
    interface TaskDao {
        @Insert
        fun insert(task: Task?)

        @Update
        fun update(task: Task?)

        @Delete
        fun delete(task: Task?)

        @Query("DELETE FROM task_table")
        fun deleteAll()

        @get:Query("SELECT * FROM task_table ORDER BY dueDate ASC")
        val allTasks: LiveData<List<Task?>?>?
    }


    class AddEditTaskActivity : AppCompatActivity() {
        private var startTimeEditText: EditText? = null
        private var dueTimeEditText: EditText? = null
        private var taskNameEditText: EditText? = null
        private var taskDescriptionEditText: EditText? = null
        private var saveTaskButton: Button? = null
        private var taskViewModel: TaskViewModel? = null
        private var taskId = -1 // -1 indicates a new task (no ID yet)

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_add_edit_task)

            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)

            if (supportActionBar != null) {
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.setDisplayShowHomeEnabled(true)
            }

            taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

            // Initialize EditText fields
            taskNameEditText = findViewById(R.id.edit_task_name)
            startTimeEditText = findViewById(R.id.edit_start_time)
            dueTimeEditText = findViewById(R.id.edit_due_time)
            taskDescriptionEditText = findViewById(R.id.edit_task_description)
            saveTaskButton = findViewById(R.id.save_task_button)

            // Check if we're editing an existing task (Intent contains task ID)
            val intent = intent
            if (intent.hasExtra("TASK_ID")) {
                taskId = intent.getIntExtra("TASK_ID", -1) // Get the task ID
                taskNameEditText.setText(intent.getStringExtra("TASK_TITLE"))
                taskDescriptionEditText.setText(intent.getStringExtra("TASK_DESCRIPTION"))
                startTimeEditText.setText(intent.getStringExtra("TASK_START_TIME"))
                dueTimeEditText.setText(intent.getStringExtra("TASK_DUE_DATE"))
            }

            startTimeEditText.setOnClickListener(View.OnClickListener { v: View? ->
                showDateTimePicker(
                    startTimeEditText
                )
            })
            dueTimeEditText.setOnClickListener(View.OnClickListener { v: View? ->
                showDateTimePicker(
                    dueTimeEditText
                )
            })

            // Save button click listener
            saveTaskButton.setOnClickListener(View.OnClickListener { v: View? ->
                if (taskId == -1) {
                    saveTask() // Add a new task
                } else {
                    updateTask(taskId) // Update the existing task
                }
            })

            taskViewModel!!.errorMessage.observe(
                this
            ) { message: String? ->
                if (message != null) {
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }

            if (supportActionBar != null) {
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            }
        }

        private fun showDateTimePicker(editText: EditText) {
            val calendar = Calendar.getInstance()

            // First show TimePicker
            TimePickerDialog(
                this,
                { view: TimePicker?, hourOfDay: Int, minute: Int ->
                    calendar[Calendar.HOUR_OF_DAY] =
                        hourOfDay
                    calendar[Calendar.MINUTE] = minute

                    // Then show DatePicker
                    DatePickerDialog(
                        this,
                        { view1: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                            calendar[Calendar.YEAR] =
                                year
                            calendar[Calendar.MONTH] = month
                            calendar[Calendar.DAY_OF_MONTH] = dayOfMonth

                            // Format final result
                            val dateTime = String.format(
                                "%02d/%02d/%04d %02d:%02d",
                                dayOfMonth, month + 1, year,
                                hourOfDay, minute
                            )
                            editText.setText(dateTime)
                        },
                        calendar[Calendar.YEAR],
                        calendar[Calendar.MONTH],
                        calendar[Calendar.DAY_OF_MONTH]
                    ).show()
                }, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], true
            ).show()
        }

        // Method to save a new task
        private fun saveTask() {
            val taskName = taskNameEditText!!.text.toString().trim { it <= ' ' }
            val startTime = startTimeEditText!!.text.toString().trim { it <= ' ' }
            val dueTime = dueTimeEditText!!.text.toString().trim { it <= ' ' }
            val taskDescription = taskDescriptionEditText!!.text.toString().trim { it <= ' ' }

            var valid = true

            if (taskName.isEmpty()) {
                taskNameEditText!!.error = "Task name is required"
                valid = false
            }
            if (startTime.isEmpty()) {
                startTimeEditText!!.error = "Start time is required"
                valid = false
            }
            if (dueTime.isEmpty()) {
                dueTimeEditText!!.error = "Due date is required"
                valid = false
            }
            if (taskDescription.isEmpty()) {
                taskDescriptionEditText!!.error = "Description is required"
                valid = false
            }

            if (!valid) {
                Toast.makeText(this, "Please correct the highlighted fields", Toast.LENGTH_SHORT).show()
                return
            }

            // Create and save the new task
            val newTask = Task(taskName, taskDescription, startTime, dueTime)
            taskViewModel!!.insert(newTask)

            // Notify user and close
            Toast.makeText(this, "Task saved", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }


        // Method to update an existing task
        private fun updateTask(taskId: Int) {
            val taskName = taskNameEditText!!.text.toString().trim { it <= ' ' }
            val startTime = startTimeEditText!!.text.toString().trim { it <= ' ' }
            val dueTime = dueTimeEditText!!.text.toString().trim { it <= ' ' }
            val taskDescription = taskDescriptionEditText!!.text.toString().trim { it <= ' ' }

            var valid = true

            if (taskName.isEmpty()) {
                taskNameEditText!!.error = "Task name is required"
                valid = false
            }
            if (startTime.isEmpty()) {
                startTimeEditText!!.error = "Start time is required"
                valid = false
            }
            if (dueTime.isEmpty()) {
                dueTimeEditText!!.error = "Due date is required"
                valid = false
            }
            if (taskDescription.isEmpty()) {
                taskDescriptionEditText!!.error = "Description is required"
                valid = false
            }

            if (!valid) {
                Toast.makeText(this, "Please correct the highlighted fields", Toast.LENGTH_SHORT).show()
                return
            }

            val task = Task(taskName, taskDescription, startTime, dueTime)
            task.id = taskId // Set the existing task ID

            // Attempt to update via ViewModel
            taskViewModel!!.update(task)

            Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            if (item.itemId == android.R.id.home) {
                finish() // Go back to the previous activity
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }


    @Entity(tableName = "task_table")
    class Task // Constructor
        (@JvmField val title: String, @JvmField val description: String, @JvmField val startDate: String, @JvmField val dueDate: String) {
        // Setter for ID (Room will auto-generate it)
        // Getters
        @JvmField
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0
    }


    class TaskRepository(application: Application) {
        private val taskDao: edu.deakin.s600152989.sit305.task41.TaskDao?
        val allTasks: LiveData<List<edu.deakin.s600152989.sit305.task41.Task?>?>?

        init {
            val db: edu.deakin.s600152989.sit305.task41.TaskDatabase = edu.deakin.s600152989.sit305.task41.TaskDatabase.Companion.getInstance(application)
            taskDao = db.taskDao()
            allTasks = taskDao.allTasks
        }

        fun insert(task: edu.deakin.s600152989.sit305.task41.Task?) {
            edu.deakin.s600152989.sit305.task41.TaskDatabase.Companion.databaseWriteExecutor.execute(Runnable {
                try {
                    taskDao!!.insert(task)
                } catch (e: Exception) {
                    throw RuntimeException("Insert failed", e)
                }
            })
        }

        fun update(task: edu.deakin.s600152989.sit305.task41.Task?) {
            edu.deakin.s600152989.sit305.task41.TaskDatabase.Companion.databaseWriteExecutor.execute(Runnable {
                try {
                    taskDao!!.update(task)
                } catch (e: Exception) {
                    throw RuntimeException("Update failed", e)
                }
            })
        }

        fun delete(task: edu.deakin.s600152989.sit305.task41.Task?) {
            edu.deakin.s600152989.sit305.task41.TaskDatabase.Companion.databaseWriteExecutor.execute(Runnable {
                try {
                    taskDao!!.delete(task)
                } catch (e: Exception) {
                    throw RuntimeException("Delete failed", e)
                }
            })
        }

        fun deleteAll() {
            edu.deakin.s600152989.sit305.task41.TaskDatabase.Companion.databaseWriteExecutor.execute(Runnable {
                try {
                    taskDao!!.deleteAll()
                } catch (e: Exception) {
                    throw RuntimeException("Delete all failed", e)
                }
            })
        }
    }


    class TaskAdapter(private var tasks: List<edu.deakin.s600152989.sit305.task41.Task>, private val listener: OnTaskClickListener?) :
        RecyclerView.Adapter<TaskViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_task, parent, false)
            return TaskViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            val currentTask = tasks[position]
            holder.textViewTitle.text = currentTask.title
            holder.textViewDescription.text = currentTask.description
            holder.textViewDueDate.text = "Due: " + currentTask.dueDate

            // Handle Edit Button click
            holder.editButton.setOnClickListener { v: View? ->
                listener?.onEditClick(currentTask)
            }

            // Handle Delete Button click
            holder.deleteButton.setOnClickListener { v: View? ->
                listener?.onDeleteClick(currentTask)
            }
        }

        override fun getItemCount(): Int {
            return tasks.size
        }

        fun setTasks(taskList: List<edu.deakin.s600152989.sit305.task41.Task>) {
            this.tasks = taskList
            notifyDataSetChanged()
        }

        fun getTaskAt(position: Int): edu.deakin.s600152989.sit305.task41.Task {
            return tasks[position]
        }

        class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewTitle: TextView =
                itemView.findViewById(R.id.textViewTitle)
            val textViewDescription: TextView =
                itemView.findViewById(R.id.textViewDescription)
            val textViewDueDate: TextView =
                itemView.findViewById(R.id.textViewDueDate)
            val editButton: Button =
                itemView.findViewById(R.id.edit_button)
            val deleteButton: Button =
                itemView.findViewById(R.id.delete_button) // Add delete button
            // Initialize delete button
        }

        // Interface for handling task click events
        interface OnTaskClickListener {
            fun onEditClick(task: edu.deakin.s600152989.sit305.task41.Task)
            fun onDeleteClick(task: edu.deakin.s600152989.sit305.task41.Task?)
        }
    }


    class TaskViewModel(application: Application) : AndroidViewModel(application) {
        private val repository =
            TaskRepository(application)
        val allTasks: LiveData<List<Task?>?>? =
            repository.allTasks
        private val errorMessage = MutableLiveData<String?>()

        fun getErrorMessage(): LiveData<String?> {
            return errorMessage
        }

        fun insert(task: Task?) {
            try {
                repository.insert(task)
            } catch (e: Exception) {
                errorMessage.postValue("Error inserting task: " + e.message)
            }
        }

        fun update(task: Task?) {
            try {
                repository.update(task)
            } catch (e: Exception) {
                errorMessage.postValue("Error updating task: " + e.message)
            }
        }

        fun delete(task: Task?) {
            try {
                repository.delete(task)
            } catch (e: Exception) {
                errorMessage.postValue("Error deleting task: " + e.message)
            }
        }

        fun deleteAllTasks() {
            try {
                repository.deleteAll()
            } catch (e: Exception) {
                errorMessage.postValue("Error deleting all tasks: " + e.message)
            }
        }
    }


}