package edu.deakin.s600152989.sit305.task41;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.taskRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        ArrayList<Task> nameList = new ArrayList<>();
        nameList.add(new Task("Alice", "Description for Alice", "01/05/2025 10:00", "02/05/2025 12:00"));
        nameList.add(new Task("Bob", "Description for Bob", "01/05/2025 13:00", "03/05/2025 15:00"));
        nameList.add(new Task("Charlie", "Description for Charlie", "02/05/2025 09:00", "04/05/2025 11:00"));


        taskAdapter = new TaskAdapter(nameList, this);
        recyclerView.setAdapter(taskAdapter);

        // Setup ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.getAllTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                // Update RecyclerView when task list changes
                taskAdapter.setTasks(tasks);
            }
        });

        // Observe LiveData to automatically update the RecyclerView
        taskViewModel.getAllTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                // This will be triggered whenever the list of tasks changes
                taskAdapter.setTasks(tasks); // Update the RecyclerView's adapter with new data
            }
        });

        // Add task FAB click listener
        FloatingActionButton fab = findViewById(R.id.fab_add_task);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            startActivity(intent);
        });
    }

    // Handle the result from AddEditTaskActivity

    public void onEditClick(Task task) {
        // Pass task data to AddEditTaskActivity for editing
        Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
        intent.putExtra("TASK_ID", task.getId());  // Pass the task ID to edit
        intent.putExtra("TASK_TITLE", task.getTitle());
        intent.putExtra("TASK_DESCRIPTION", task.getDescription());
        intent.putExtra("TASK_START_TIME", task.getStartDate());
        intent.putExtra("TASK_DUE_DATE", task.getDueDate());

        startActivity(intent);
    }


    public void onDeleteClick(Task task) {
        // Handle delete action
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    taskViewModel.delete(task);  // Delete task from ViewModel
                    Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show();
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
        }
    }
}
