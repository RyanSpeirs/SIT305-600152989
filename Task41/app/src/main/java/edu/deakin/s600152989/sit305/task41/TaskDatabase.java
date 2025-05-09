package edu.deakin.s600152989.sit305.task41;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Database(entities = {Task.class}, version = 1, exportSchema = false)  // No versioning needed
public abstract class TaskDatabase extends RoomDatabase {

    // Executor for background database operations
    public static final Executor databaseWriteExecutor = Executors.newFixedThreadPool(4);

    private static volatile TaskDatabase instance;

    public abstract TaskDao taskDao();

    // Get instance of the database (Singleton pattern)
    public static synchronized TaskDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (TaskDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    TaskDatabase.class,
                                    "task_database"
                            )
                            .allowMainThreadQueries()  // Allow main thread queries (if needed, for testing)
                            .build(); // No migrations or fallbacks needed here
                }
            }
        }
        return instance;
    }
}

