package edu.deakin.s600152989.sit305.task41

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

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
