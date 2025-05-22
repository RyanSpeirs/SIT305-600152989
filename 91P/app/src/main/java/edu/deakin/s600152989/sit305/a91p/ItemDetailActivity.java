package edu.deakin.s600152989.sit305.a91p;

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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class ItemDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView itemTitleDetail, itemDescriptionDetail, itemDateDetail, itemLocationDetail, itemContactDetail, itemStatusDetail;
    private Button backButton, deleteButton;
    private LostFoundViewModel lostFoundViewModel;
    private LostFoundItem item;
    private GoogleMap map;


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

        // Initialize the map fragment and the map itself
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // If the item has a valid location, add a marker to the map
        if (item != null) {
            String[] latlong =  item.getLocation().split(",");
            double latitude = Double.parseDouble(latlong[0]);
            double longitude = Double.parseDouble(latlong[1]);
            LatLng itemLocation = new LatLng(latitude, longitude);
            map.addMarker(new MarkerOptions()
                    .position(itemLocation)
                    .title(item.getTitle())
                    .snippet(item.getDescription())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(itemLocation, 15));  // Zoom to the item's location
        }
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