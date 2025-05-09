package edu.deakin.s600152989.sit305.task41

import android.app.Application
import androidx.lifecycle.LiveData

class TaskRepository(application: Application) {
    private val taskDao: TaskDao?
    val allTasks: LiveData<List<Task?>?>?

    init {
        val db: TaskDatabase = TaskDatabase.Companion.getInstance(application)
        taskDao = db.taskDao()
        allTasks = taskDao.allTasks
    }

    fun insert(task: Task?) {
        TaskDatabase.Companion.databaseWriteExecutor.execute(Runnable {
            try {
                taskDao!!.insert(task)
            } catch (e: Exception) {
                throw RuntimeException("Insert failed", e)
            }
        })
    }

    fun update(task: Task?) {
        TaskDatabase.Companion.databaseWriteExecutor.execute(Runnable {
            try {
                taskDao!!.update(task)
            } catch (e: Exception) {
                throw RuntimeException("Update failed", e)
            }
        })
    }

    fun delete(task: Task?) {
        TaskDatabase.Companion.databaseWriteExecutor.execute(Runnable {
            try {
                taskDao!!.delete(task)
            } catch (e: Exception) {
                throw RuntimeException("Delete failed", e)
            }
        })
    }

    fun deleteAll() {
        TaskDatabase.Companion.databaseWriteExecutor.execute(Runnable {
            try {
                taskDao!!.deleteAll()
            } catch (e: Exception) {
                throw RuntimeException("Delete all failed", e)
            }
        })
    }
}