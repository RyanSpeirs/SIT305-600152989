package edu.deakin.s600152989.sit305.a71p;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private Button viewListingsButton;  // Declare the Button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        // Initialize the Add Item button
        Button addButton = findViewById(R.id.addNewItemButton);
        addButton.setOnClickListener(v -> {
            // Open AddItemActivity to allow the user to add a new item
            startActivity(new Intent(MainActivity.this, AddItemActivity.class));
        });

        // Initialize the "See Current Listings" button
        viewListingsButton = findViewById(R.id.viewListingsButton);  // Link the Button to the layout
        viewListingsButton.setOnClickListener(v -> {
            // Open ListingsActivity to see current listings
            startActivity(new Intent(MainActivity.this, ListingsActivity.class));
        });
    }
}

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class AddItemActivity extends AppCompatActivity {

    private EditText titleEditText, dateEditText, descriptionEditText, locationEditText, contactEditText;
    private RadioGroup typeRadioGroup;
    private LostFoundViewModel lostFoundViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Initialize the ViewModel
        lostFoundViewModel = new ViewModelProvider(this).get(LostFoundViewModel.class);

        // Reference the EditText views
        titleEditText = findViewById(R.id.titleEditText);
        dateEditText = findViewById(R.id.dateEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        locationEditText = findViewById(R.id.locationEditText);
        contactEditText = findViewById(R.id.contactEditText);
        typeRadioGroup = findViewById(R.id.typeRadioGroup);

        // Set click listener on the Save button
        findViewById(R.id.saveButton).setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String date = dateEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String location = locationEditText.getText().toString();
            String contact = contactEditText.getText().toString();

            // Validate input fields and show a Toast message for empty fields
            if (title.isEmpty() || date.isEmpty() || description.isEmpty() || location.isEmpty()) {
                Toast.makeText(AddItemActivity.this, "All fields must be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Determine item type from RadioGroup
            int selectedTypeId = typeRadioGroup.getCheckedRadioButtonId();
            String type = "Lost"; // default
            if (selectedTypeId == R.id.radioFound) {
                type = "Found";
            }

            // Create a new LostFoundItem object with the input data
            LostFoundItem item = new LostFoundItem(title, description, date, location, contact, type);

            // Use the ViewModel to insert the item into the database
            lostFoundViewModel.insert(item);

            // Close the activity
            finish();
        });

        // Set up the "Back" button logic
        findViewById(R.id.backButton).setOnClickListener(v -> {
            // Simply finish the activity to go back
            finish();

        });
    }
}


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LostFoundAdapter adapter;
    private LostFoundViewModel lostFoundViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);

        // Set up RecyclerView
        recyclerView = findViewById(R.id.itemsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up Adapter with item click listener
        adapter = new LostFoundAdapter(item -> {
            Intent intent = new Intent(ListingsActivity.this, ItemDetailActivity.class);
            intent.putExtra("item", item);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // ViewModel and LiveData observation
        lostFoundViewModel = new ViewModelProvider(this).get(LostFoundViewModel.class);
        lostFoundViewModel.getAllItems().observe(this, items -> adapter.submitList(items));

        // Back button setup
        Button itemsBackButton = findViewById(R.id.itemsBackButton);
        itemsBackButton.setText("Back");  // Optional: Set text here or in XML
        itemsBackButton.setOnClickListener(v -> {
            startActivity(new Intent(ListingsActivity.this, MainActivity.class));
            finish();  // Prevents user from returning to this activity with the back button
        });
    }
}

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AlertDialog;

public class ItemDetailActivity extends AppCompatActivity {

    private TextView itemTitleDetail, itemDescriptionDetail, itemDateDetail, itemLocationDetail, itemContactDetail, itemStatusDetail;
    private Button backButton, deleteButton;
    private LostFoundViewModel lostFoundViewModel;
    private LostFoundItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // Initialize the UI elements
        itemTitleDetail = findViewById(R.id.itemTitleDetail);
        itemDescriptionDetail = findViewById(R.id.itemDescriptionDetail);
        itemDateDetail = findViewById(R.id.itemDateDetail);
        itemLocationDetail = findViewById(R.id.itemLocationDetail);
        itemContactDetail = findViewById(R.id.itemContactDetail);
        itemStatusDetail = findViewById(R.id.itemStatusDetail);
        backButton = findViewById(R.id.backButton);
        deleteButton = findViewById(R.id.deleteButton);



        // Initialize ViewModel
        lostFoundViewModel = new ViewModelProvider(this).get(LostFoundViewModel.class);

        // Get the item passed via the intent (if the object is Serializable)
        item = (LostFoundItem) getIntent().getSerializableExtra("item");

        // Ensure the item is passed and update the UI
        if (item != null) {
            itemTitleDetail.setText(item.getTitle());
            itemDescriptionDetail.setText(item.getDescription());
            itemDateDetail.setText(item.getDate());
            itemLocationDetail.setText(item.getLocation());
            itemContactDetail.setText("Contact: " + item.getContact());
            itemStatusDetail.setText("Status: " + item.getType());

            // Set the color based on item type (Lost/Found)
            if ("Found".equalsIgnoreCase(item.getType())) {
                itemStatusDetail.setTextColor(ContextCompat.getColor(this, R.color.blue));  // Blue color for "Found"
            } else {
                itemStatusDetail.setTextColor(ContextCompat.getColor(this, R.color.red));   // Red color for "Lost"
            }
        }

        // Set OnClickListener for the Back button
        backButton.setOnClickListener(v -> finish());  // Just go back to the previous activity

        // Handle the Delete button click
        deleteButton.setOnClickListener(v -> {
            if (item != null) {
                showDeleteConfirmationDialog();
            }
        });
    }

    // Confirmation dialog before deleting the item
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Perform the delete operation via ViewModel
                    lostFoundViewModel.delete(item);
                    Toast.makeText(ItemDetailActivity.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                    navigateToListingsActivity();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Navigate back to ListingsActivity after deletion
    private void navigateToListingsActivity() {
        finish();  // Finish current activity first
        Intent intent = new Intent(ItemDetailActivity.this, ListingsActivity.class);
        startActivity(intent);
    }
}


import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class LostFoundViewModel extends AndroidViewModel {

    private LostFoundRepository repository;
    private LiveData<List<LostFoundItem>> allItems;

    public LostFoundViewModel(Application application) {
        super(application);
        repository = new LostFoundRepository(application);
        allItems = repository.getAllItems();
    }

    public LiveData<List<LostFoundItem>> getAllItems() {
        return allItems;
    }

    public void insert(LostFoundItem item) {
        repository.insert(item);
    }

    public void delete(LostFoundItem item) {
        repository.delete(item);
    }
}


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "lost_found_items")
public class LostFoundItem  implements Serializable {

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

    // Override equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LostFoundItem that = (LostFoundItem) o;
        return id == that.id &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(date, that.date) &&
                Objects.equals(location, that.location) &&
                Objects.equals(contact, that.contact) &&
                Objects.equals(type, that.type);
    }

    // Override hashCode method
    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, date, location, contact, type);
    }
}


package edu.deakin.s600152989.sit305.a71p;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class LostFoundAdapter extends ListAdapter<LostFoundItem, LostFoundAdapter.LostFoundViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(LostFoundItem item);
    }

    private final OnItemClickListener listener;

    public LostFoundAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<LostFoundItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<LostFoundItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull LostFoundItem oldItem, @NonNull LostFoundItem newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull LostFoundItem oldItem, @NonNull LostFoundItem newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public LostFoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lost_found, parent, false);
        return new LostFoundViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LostFoundViewHolder holder, int position) {
        LostFoundItem currentItem = getItem(position);
        holder.titleTextView.setText(currentItem.getTitle());
        holder.dateTextView.setText(currentItem.getDate());
        holder.statusTextView.setText(currentItem.getType());

        // Set the color based on the status
        if ("Found".equalsIgnoreCase(currentItem.getType())) {
            holder.statusTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.blue));  // Found items will be blue
        } else {
            holder.statusTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));  // Lost items will be red
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> listener.onItemClick(currentItem));
    }


    public static class LostFoundViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView dateTextView;
        private final TextView statusTextView;

        public LostFoundViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.itemTitle);
            dateTextView = itemView.findViewById(R.id.itemDate);
            statusTextView = itemView.findViewById(R.id.itemStatus);
        }
    }
}

package edu.deakin.s600152989.sit305.a71p;
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
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    LostFoundDatabase.class,
                                    "lost_found_database" // Database name
                            ).fallbackToDestructiveMigration() // Automatically destroy the database when migrating
                            .build();
                }
            }
        }
        return instance;
    }
}

package edu.deakin.s600152989.sit305.a71p;

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


