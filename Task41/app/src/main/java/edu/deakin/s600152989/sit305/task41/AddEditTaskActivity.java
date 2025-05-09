package edu.deakin.s600152989.sit305.task41;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;

public class AddEditTaskActivity extends AppCompatActivity {

    private EditText startTimeEditText;
    private EditText dueTimeEditText;
    private EditText taskNameEditText;
    private EditText taskDescriptionEditText;
    private Button saveTaskButton;
    private TaskViewModel taskViewModel;
    private int taskId = -1; // -1 indicates a new task (no ID yet)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Initialize EditText fields
        taskNameEditText = findViewById(R.id.edit_task_name);
        startTimeEditText = findViewById(R.id.edit_start_time);
        dueTimeEditText = findViewById(R.id.edit_due_time);
        taskDescriptionEditText = findViewById(R.id.edit_task_description);
        saveTaskButton = findViewById(R.id.save_task_button);

        // Check if we're editing an existing task (Intent contains task ID)
        Intent intent = getIntent();
        if (intent.hasExtra("TASK_ID")) {
            taskId = intent.getIntExtra("TASK_ID", -1); // Get the task ID
            taskNameEditText.setText(intent.getStringExtra("TASK_TITLE"));
            taskDescriptionEditText.setText(intent.getStringExtra("TASK_DESCRIPTION"));
            startTimeEditText.setText(intent.getStringExtra("TASK_START_TIME"));
            dueTimeEditText.setText(intent.getStringExtra("TASK_DUE_DATE"));
        }

        startTimeEditText.setOnClickListener(v -> showDateTimePicker(startTimeEditText));
        dueTimeEditText.setOnClickListener(v -> showDateTimePicker(dueTimeEditText));

        // Save button click listener
        saveTaskButton.setOnClickListener(v -> {
            if (taskId == -1) {
                saveTask(); // Add a new task
            } else {
                updateTask(taskId); // Update the existing task
            }
        });

        taskViewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void showDateTimePicker(EditText editText) {
        final Calendar calendar = Calendar.getInstance();

        // First show TimePicker
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            // Then show DatePicker
            new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Format final result
                String dateTime = String.format("%02d/%02d/%04d %02d:%02d",
                        dayOfMonth, month + 1, year,
                        hourOfDay, minute);
                editText.setText(dateTime);

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    // Method to save a new task
    private void saveTask() {
        String taskName = taskNameEditText.getText().toString().trim();
        String startTime = startTimeEditText.getText().toString().trim();
        String dueTime = dueTimeEditText.getText().toString().trim();
        String taskDescription = taskDescriptionEditText.getText().toString().trim();

        boolean valid = true;

        if (taskName.isEmpty()) {
            taskNameEditText.setError("Task name is required");
            valid = false;
        }
        if (startTime.isEmpty()) {
            startTimeEditText.setError("Start time is required");
            valid = false;
        }
        if (dueTime.isEmpty()) {
            dueTimeEditText.setError("Due date is required");
            valid = false;
        }
        if (taskDescription.isEmpty()) {
            taskDescriptionEditText.setError("Description is required");
            valid = false;
        }

        if (!valid) {
            Toast.makeText(this, "Please correct the highlighted fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create and save the new task
        Task newTask = new Task(taskName, taskDescription, startTime, dueTime);
        taskViewModel.insert(newTask);

        // Notify user and close
        Toast.makeText(this, "Task saved", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }


    // Method to update an existing task
    private void updateTask(int taskId) {
        String taskName = taskNameEditText.getText().toString().trim();
        String startTime = startTimeEditText.getText().toString().trim();
        String dueTime = dueTimeEditText.getText().toString().trim();
        String taskDescription = taskDescriptionEditText.getText().toString().trim();

        boolean valid = true;

        if (taskName.isEmpty()) {
            taskNameEditText.setError("Task name is required");
            valid = false;
        }
        if (startTime.isEmpty()) {
            startTimeEditText.setError("Start time is required");
            valid = false;
        }
        if (dueTime.isEmpty()) {
            dueTimeEditText.setError("Due date is required");
            valid = false;
        }
        if (taskDescription.isEmpty()) {
            taskDescriptionEditText.setError("Description is required");
            valid = false;
        }

        if (!valid) {
            Toast.makeText(this, "Please correct the highlighted fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task = new Task(taskName, taskDescription, startTime, dueTime);
        task.setId(taskId);  // Set the existing task ID

        // Attempt to update via ViewModel
        taskViewModel.update(task);

        Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Go back to the previous activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
