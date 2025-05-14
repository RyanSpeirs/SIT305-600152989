package edu.deakin.s600152989.sit305.a91p;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.Arrays;
import java.util.List;


public class AddItemActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private LatLng selectedLatLng;
    private Marker currentMarker;
    private FusedLocationProviderClient fusedLocationClient;

    private EditText titleEditText, dateEditText, descriptionEditText, locationEditText, contactEditText;
    private RadioGroup typeRadioGroup;
    private LostFoundViewModel lostFoundViewModel;
    private double selectedLatitude = 0.0;
    private double selectedLongitude = 0.0;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Initialize Views
        titleEditText = findViewById(R.id.titleEditText);
        dateEditText = findViewById(R.id.dateEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        locationEditText = findViewById(R.id.locationEditText);
        contactEditText = findViewById(R.id.contactEditText);
        typeRadioGroup = findViewById(R.id.typeRadioGroup);

        // Initialize the ViewModel
        lostFoundViewModel = new ViewModelProvider(this).get(LostFoundViewModel.class);


        // Set click listener on the Save button
        findViewById(R.id.saveButton).setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String date = dateEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String location = locationEditText.getText().toString();
            String contact = contactEditText.getText().toString();

            if (title.isEmpty() || date.isEmpty() || description.isEmpty() || location.isEmpty()) {
                Toast.makeText(AddItemActivity.this, "All fields must be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedTypeId = typeRadioGroup.getCheckedRadioButtonId();
            String type = (selectedTypeId == R.id.radioFound) ? "Found" : "Lost";

            LostFoundItem item = new LostFoundItem(title, description, date, location, contact, type, selectedLatitude, selectedLongitude);
            lostFoundViewModel.insert(item);

            finish();
        });

        // Set up the "Back" button logic
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Get Current Location Button
        Button getLocationButton = findViewById(R.id.getLocationButton);
        getLocationButton.setOnClickListener(v -> fetchAndSetCurrentLocation());

        // Set up fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set up the Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.itemMapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                map = googleMap;
                enableLocationOnMap();
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Set default location (e.g., Deakin)
        LatLng defaultLocation = new LatLng(-38.197789, 144.115265);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 14));

        // Handle map tap to select item location
        map.setOnMapClickListener(latLng -> {
            selectedLatLng = latLng;
            if (currentMarker != null) currentMarker.remove();

            currentMarker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Selected Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        });

        // Enable My Location if permission granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void fetchAndSetCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null && map != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                map.clear(); // Clear previous markers
                map.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

                // Optionally use Geocoder to convert to address
                String coords = location.getLatitude() + ", " + location.getLongitude();
                locationEditText.setText(coords);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchAndSetCurrentLocation();
        }
    }

    private void enableLocationOnMap() {
        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Enable location on the map
            map.setMyLocationEnabled(true);

            // Get the user's current location and update the map
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));  // Move camera to user's location
                }
            });
        } else {
            // If location permission is not granted, request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
}
