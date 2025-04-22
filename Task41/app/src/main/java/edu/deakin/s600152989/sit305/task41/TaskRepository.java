package edu.deakin.s600152989.sit305.task41;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskRepository {

    private TaskDao taskDao;
    private LiveData<List<Task>> allTasks;

    public TaskRepository(Application application) {
        TaskDatabase db = TaskDatabase.getInstance(application);
        taskDao = db.taskDao();
        allTasks = taskDao.getAllTasks();
    }

    // Get all tasks from the database
    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    // Insert a task into the database
    public void insert(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.insert(task));
    }

    // Update a task in the database
    public void update(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.update(task));
    }

    // Delete a task from the database
    public void delete(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.delete(task));
    }

    // Delete all tasks from the database
    public void deleteAll() {
        TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.deleteAll());
    }
}
