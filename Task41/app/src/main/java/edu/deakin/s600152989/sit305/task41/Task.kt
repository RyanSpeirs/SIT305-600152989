package edu.deakin.s600152989.sit305.task41

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
class Task // Constructor
    (@JvmField val title: String, @JvmField val description: String, @JvmField val startDate: String, @JvmField val dueDate: String) {
    // Setter for ID (Room will auto-generate it)
    // Getters
    @JvmField
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

