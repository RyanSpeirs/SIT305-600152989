package edu.deakin.s600152989.sit305.a91p;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.io.File;


public class MainActivity extends AppCompatActivity {

    private Button viewListingsButton;  // Declare the Button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        deleteCorruptedSqliteCacheAsync(this);
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

        Button showOnMapButton = findViewById(R.id.showOnMapButton);
        showOnMapButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapViewActivity.class);
            startActivity(intent);
        });
    }

    private void deleteCorruptedSqliteCacheAsync(Context context) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            File dbDir = new File(context.getDatabasePath("dummy").getParent());
            if (dbDir != null && dbDir.isDirectory()) {
                for (File file : dbDir.listFiles()) {
                    String name = file.getName();
                    if (name.contains("tile") || name.contains("map") || name.endsWith(".db")) {
                        Log.d("DB_CLEANUP", "Deleting: " + name);
                        file.delete();
                    }
                }
            }
        });
    }
}
