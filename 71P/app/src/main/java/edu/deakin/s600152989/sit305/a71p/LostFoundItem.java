package edu.deakin.s600152989.sit305.a71p;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lost_found_items")
public class LostFoundItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private String date;
    private String location;
    private String contact;
    private String type; // "Lost" or "Found"

    // Constructor
    public LostFoundItem(String title, String description, String date, String location, String contact, String type) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.contact = contact;
        this.type = type;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

