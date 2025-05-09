package edu.deakin.s600152989.sit305.a71p;

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

    // Get all LostFoundItems
    @Query("SELECT * FROM lost_found_items")
    List<LostFoundItem> getAllItems();

    // Get items by type (Lost or Found)
    @Query("SELECT * FROM lost_found_items WHERE type = :type")
    List<LostFoundItem> getItemsByType(String type);
}
