package edu.deakin.s600152989.sit305.task41

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import java.util.Calendar

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
