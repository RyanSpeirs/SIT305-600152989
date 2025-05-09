package edu.deakin.s600152989.sit305.a71p;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import edu.deakin.s600152989.sit305.a71p.LostFoundViewModel;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LostFoundAdapter adapter;
    private Button viewListingsButton;  // Declare the Button
    private LostFoundViewModel lostFoundViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize the RecyclerView and its adapter
        recyclerView = findViewById(R.id.itemsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LostFoundAdapter();
        recyclerView.setAdapter(adapter);

        // Initialize ViewModel to fetch data
        lostFoundViewModel = new ViewModelProvider(this).get(LostFoundViewModel.class);
        lostFoundViewModel.getAllItems().observe(this, items -> {
            // Update the UI with the data
            adapter.submitList(items);
        });

        // Initialize the Add Item button
        FloatingActionButton addButton = findViewById(R.id.addNewItemButton);
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
}