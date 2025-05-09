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

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public void insert(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            try {
                taskDao.insert(task);
            } catch (Exception e) {
                throw new RuntimeException("Insert failed", e);
            }
        });
    }

    public void update(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            try {
                taskDao.update(task);
            } catch (Exception e) {
                throw new RuntimeException("Update failed", e);
            }
        });
    }

    public void delete(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            try {
                taskDao.delete(task);
            } catch (Exception e) {
                throw new RuntimeException("Delete failed", e);
            }
        });
    }

    public void deleteAll() {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            try {
                taskDao.deleteAll();
            } catch (Exception e) {
                throw new RuntimeException("Delete all failed", e);
            }
        });
    }
}