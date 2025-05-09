package edu.deakin.s600152989.sit305.a71p;

import android.os.Bundle;
import androidx.activity.viewModels;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.deakin.s600152989.sit305.a71p.LostFoundAdapter;
import edu.deakin.s600152989.sit305.a71p.LostFoundItem;
import edu.deakin.s600152989.sit305.a71p.LostFoundViewModel;

public class ListingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LostFoundAdapter adapter;
    private LostFoundViewModel lostFoundViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listings);

        recyclerView = findViewById(R.id.itemsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LostFoundAdapter();
        recyclerView.setAdapter(adapter);

        lostFoundViewModel = new LostFoundViewModel(); // Initialize the ViewModel
        lostFoundViewModel.getAllItems().observe(this, items -> {
            // Update the RecyclerView with the list of items
            adapter.submitList(items);
        });
    }
}