package edu.deakin.s600152989.sit305.a91p;
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {LostFoundItem.class}, version = 1)
public abstract class LostFoundDatabase extends RoomDatabase {

    // Singleton instance of the database
    private static volatile LostFoundDatabase instance;

    // Abstract method to get the DAO
    public abstract LostFoundDao lostFoundDao();

    // Get the instance of the database (Singleton)
    public static LostFoundDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (LostFoundDatabase.class) {
                if (instance == null) {
                    // Build the database instance
                    Builder<LostFoundDatabase> lostFoundDatabase = Room.databaseBuilder(
                            context.getApplicationContext(),
                            LostFoundDatabase.class,
                            "lost_found_database" // Database name
                    );
                    lostFoundDatabase.fallbackToDestructiveMigration();// Database name
                    instance = lostFoundDatabase // Automatically destroy the database when migrating
                            .build();
                }
            }
        }
        return instance;
    }
}
