package edu.deakin.s600152989.sit305.a91p;

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
