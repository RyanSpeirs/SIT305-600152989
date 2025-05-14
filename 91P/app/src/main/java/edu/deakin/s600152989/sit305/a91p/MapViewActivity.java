package edu.deakin.s600152989.sit305.a91p;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private LostFoundViewModel lostFoundViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        // Get ViewModel
        lostFoundViewModel = new ViewModelProvider(this).get(LostFoundViewModel.class);

        // Set up map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Map fragment not found", Toast.LENGTH_SHORT).show();
        }

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());  // Closes this activity, returns to MainActivity
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Go back to MainActivity
        return true;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Observe LiveData from ViewModel for database changes
        lostFoundViewModel.getAllItems().observe(this, new Observer<List<LostFoundItem>>() {
            @Override
            public void onChanged(List<LostFoundItem> items) {
                // Clear previous markers
                map.clear();

                // Add markers for each item in the list
                for (LostFoundItem item : items) {
                    LatLng position = new LatLng(item.getLatitude(), item.getLongitude());
                    float color = item.getType().equalsIgnoreCase("Lost") ?
                            BitmapDescriptorFactory.HUE_RED :
                            BitmapDescriptorFactory.HUE_GREEN;

                    map.addMarker(new MarkerOptions()
                            .position(position)
                            .title(item.getTitle())
                            .snippet(item.getDescription())  // Optional: display description
                            .icon(BitmapDescriptorFactory.defaultMarker(color)));
                }

                // Move camera to the first item or a default location
                if (!items.isEmpty()) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(items.get(0).getLatitude(), items.get(0).getLongitude()), 12));
                } else {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(-37.847565, 144.0), 10)); // Default to Deakin area
                }
            }
        });
    }
}