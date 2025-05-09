package edu.deakin.s600152989.sit305.task41;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import androidx.lifecycle.MutableLiveData;

public class TaskViewModel extends AndroidViewModel {

    private TaskRepository repository;
    private LiveData<List<Task>> allTasks;
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public TaskViewModel(Application application) {
        super(application);
        repository = new TaskRepository(application);
        allTasks = repository.getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void insert(Task task) {
        try {
            repository.insert(task);
        } catch (Exception e) {
            errorMessage.postValue("Error inserting task: " + e.getMessage());
        }
    }

    public void update(Task task) {
        try {
            repository.update(task);
        } catch (Exception e) {
            errorMessage.postValue("Error updating task: " + e.getMessage());
        }
    }

    public void delete(Task task) {
        try {
            repository.delete(task);
        } catch (Exception e) {
            errorMessage.postValue("Error deleting task: " + e.getMessage());
        }
    }

    public void deleteAllTasks() {
        try {
            repository.deleteAll();
        } catch (Exception e) {
            errorMessage.postValue("Error deleting all tasks: " + e.getMessage());
        }
    }
}
