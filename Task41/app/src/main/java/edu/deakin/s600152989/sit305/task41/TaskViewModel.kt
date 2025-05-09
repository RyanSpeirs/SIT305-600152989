package edu.deakin.s600152989.sit305.task41

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

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
