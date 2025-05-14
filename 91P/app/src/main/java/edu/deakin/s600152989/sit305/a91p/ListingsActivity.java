package edu.deakin.s600152989.sit305.a91p;

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
