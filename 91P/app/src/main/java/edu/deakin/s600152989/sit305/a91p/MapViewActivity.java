package edu.deakin.s600152989.sit305.a91p;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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

        Button zoomInButton = findViewById(R.id.zoomInButton);
        Button zoomOutButton = findViewById(R.id.zoomOutButton);

        zoomInButton.setOnClickListener(v -> {
            map.animateCamera(CameraUpdateFactory.zoomIn());
        });

        zoomOutButton.setOnClickListener(v -> {
            map.animateCamera(CameraUpdateFactory.zoomOut());
        });
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);  // Show blue dot and button
            map.getUiSettings().setMyLocationButtonEnabled(true);  // Show "My Location" button
        }
        map.setMyLocationEnabled(true);  // Show blue dot and button
        map.getUiSettings().setMyLocationButtonEnabled(true);  // Show "My Location" button


        // Observe LiveData from ViewModel for database changes
        /*lostFoundViewModel.getAllItems().observe(this, new Observer<List<LostFoundItem>>() {
            @Override
            public void onChanged(List<LostFoundItem> items) {
                // Clear previous markers
                map.clear();

                // Add markers for each item in the list
                for (LostFoundItem item : items) {
                    LatLng position = new LatLng(item.getLatitude(), item.getLongitude());
                    float color = item.getType().equalsIgnoreCase("Lost") ?
                            BitmapDescriptorFactory.HUE_RED :
                            BitmapDescriptorFactory.HUE_BLUE;

                    map.addMarker(new MarkerOptions()
                            .position(position)
                            .title(item.getTitle())
                            .snippet(item.getDescription())  // Optional: display description
                            .icon(BitmapDescriptorFactory.defaultMarker(color)));
                }

                // Move camera to the first item or a default location
                if (!items.isEmpty()) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(37.4219983, -122.084), 10)); // Used to be Deakin, but current location is always Googleplex in Mountain View California
                } else {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(37.4219983, -122.084), 10)); // Used to be Deakin, but current location is always Googleplex in Mountain View California
                }
            }
           });*/
        lostFoundViewModel.getAllItems().observe(this, new Observer<List<LostFoundItem>>() {
            @Override
            public void onChanged(List<LostFoundItem> items) {
                // Log the size of the list to ensure it's getting updated
                Log.d("MAP_VIEW", "Number of items: " + (items != null ? items.size() : "null"));

                // Clear the existing markers
                map.clear();

                if (items != null && !items.isEmpty()) {
                    for (LostFoundItem item : items) {
                        Log.d("MAP_VIEW", "Adding marker for: " + item.getTitle() + " at " + item.getLatitude() + ", " + item.getLongitude());

                        String[] latlong =  item.getLocation().split(",");
                        double latitude = Double.parseDouble(latlong[0]);
                        double longitude = Double.parseDouble(latlong[1]);

                        LatLng position = new LatLng(latitude, longitude);
                        float color = item.getType().equalsIgnoreCase("Lost") ?
                                BitmapDescriptorFactory.HUE_RED :
                                BitmapDescriptorFactory.HUE_BLUE;

                        map.addMarker(new MarkerOptions()
                                .position(position)
                                .title(item.getTitle())
                                .snippet(item.getDescription())
                                .icon(BitmapDescriptorFactory.defaultMarker(color)));
                    }

                    // Zoom to the first item's location if there are items
                    LostFoundItem firstItem = items.get(0);
                    String[] firstLatLong = firstItem.getLocation().split(",");
                    double firstLat = Double.parseDouble(firstLatLong[0]);
                    double firstLng = Double.parseDouble(firstLatLong[1]);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(firstLat, firstLng), 15));
                } else {
                    // No items — get current location
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                            if (location != null) {
                                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17));
                            } else {
                                Log.w("MAP_VIEW", "Current location is null. Falling back to default.");
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.4219983, -122.084), 17));
                            }
                        });
                    } else {
                        // Permission not granted
                        Log.w("MAP_VIEW", "Location permission not granted. Falling back to default.");
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.4219983, -122.084), 17));
                    }
                }
            }
        });
        /*
        To constructs a LatLng with the given latitude and longitude coordinates */

        // LatLng location = new LatLng(latitude, longitude);




    }



}