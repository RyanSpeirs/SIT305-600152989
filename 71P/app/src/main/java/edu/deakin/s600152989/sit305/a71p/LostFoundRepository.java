package edu.deakin.s600152989.sit305.a71p;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LostFoundRepository {

    private final LostFoundDao lostFoundDao;
    private final LiveData<List<LostFoundItem>> allItems;
    private final ExecutorService executorService;

    // Constructor initializes the DAO and gets all items from the database
    public LostFoundRepository(Application application) {
        LostFoundDatabase database = LostFoundDatabase.getInstance(application);
        lostFoundDao = database.lostFoundDao();
        allItems = lostFoundDao.getAllItems();  // LiveData to observe the items
        executorService = Executors.newSingleThreadExecutor(); // For background operations
    }

    // Returns all items as LiveData
    public LiveData<List<LostFoundItem>> getAllItems() {
        return allItems;
    }

    // Inserts a LostFoundItem in the background thread
    public void insert(LostFoundItem item) {
        executorService.execute(() -> lostFoundDao.insert(item));  // Perform insert on a background thread
    }

    // Deletes a LostFoundItem in the background thread
    public void delete(LostFoundItem item) {
        executorService.execute(() -> lostFoundDao.delete(item));  // Perform delete on a background thread
    }

    // Get items by type ("Lost" or "Found") as LiveData
    public LiveData<List<LostFoundItem>> getItemsByType(String type) {
        return lostFoundDao.getItemsByType(type);
    }
}
