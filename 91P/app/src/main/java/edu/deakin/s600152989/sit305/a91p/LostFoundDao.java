package edu.deakin.s600152989.sit305.a91p;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LostFoundDao {

    // Insert a LostFoundItem into the database
    @Insert
    void insert(LostFoundItem lostFoundItem);

    // Delete a LostFoundItem from the database
    @Delete
    void delete(LostFoundItem lostFoundItem);

    // Get all items from the database
    @Query("SELECT * FROM lost_found_items")  // Updated table name
    LiveData<List<LostFoundItem>> getAllItems();

    // Optional: Get items by type (Lost or Found)
    @Query("SELECT * FROM lost_found_items WHERE type = :type")  // Updated table name
    LiveData<List<LostFoundItem>> getItemsByType(String type);
}
