package edu.deakin.s600152989.sit305.task41

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.concurrent.Volatile

@Database(entities = [Task::class], version = 1, exportSchema = false) // No versioning needed
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao?

    companion object {
        // Executor for background database operations
        val databaseWriteExecutor: Executor = Executors.newFixedThreadPool(4)

        @Volatile
        private var instance: TaskDatabase? = null

        // Get instance of the database (Singleton pattern)
        @Synchronized
        fun getInstance(context: Context): TaskDatabase? {
            if (instance == null) {
                synchronized(TaskDatabase::class.java) {
                    if (instance == null) {
                        instance = databaseBuilder(
                            context.applicationContext,
                            TaskDatabase::class.java,
                            "task_database"
                        )
                            .allowMainThreadQueries() // Allow main thread queries (if needed, for testing)
                            .build() // No migrations or fallbacks needed here
                    }
                }
            }
            return instance
        }
    }
}

