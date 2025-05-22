//  Ryan Speirs 600152989
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

package edu.deakin.s600152989.sit305.a91p;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import java.io.File;

public class CleanUp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        deleteCorruptedSqliteCache(this);
    }

    private void deleteCorruptedSqliteCache(Context context) {
        File dbDir = new File(context.getDatabasePath("dummy").getParent());
        for (File file : dbDir.listFiles()) {
            String name = file.getName();
            if (name.contains("tile") || name.contains("map") || name.endsWith(".db")) {
                Log.d("DB_CLEANUP", "Deleting: " + name);
                file.delete();
            }
        }
    }
}


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
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(firstLat, firstLng), 17));
                } else {
                    // No items — get current location
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                            if (location != null) {
                                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20));
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

package edu.deakin.s600152989.sit305.a91p;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;
import java.util.List;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    enableLocationOnMap();  // Permission granted
                } else {
                    Toast.makeText(getContext(), "Permission denied. Cannot access location.", Toast.LENGTH_SHORT).show();
                }
            });

    private LostFoundViewModel lostFoundViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_map_view, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_fragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        lostFoundViewModel = new ViewModelProvider(requireActivity()).get(LostFoundViewModel.class);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableLocationOnMap();
        } else {
            // Request location permission using modern launcher
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void enableLocationOnMap() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && mMap != null) {

            mMap.setMyLocationEnabled(true);

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17));
                        }
                    });
        }
    }
}

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

package edu.deakin.s600152989.sit305.a91p;

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
    private double latitude;
    private double longitude;

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    // Constructor
    public LostFoundItem(String title, String description, String date, String location,
                         String contact, String type, double latitude, double longitude) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
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
                Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
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
        return Objects.hash(id, title, description, date, location, contact, type, latitude, longitude);
    }
}

package edu.deakin.s600152989.sit305.a91p;

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

package edu.deakin.s600152989.sit305.a91p;

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

package edu.deakin.s600152989.sit305.a91p;

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

package edu.deakin.s600152989.sit305.a91p;

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

package edu.deakin.s600152989.sit305.a91p;
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
                    Builder<LostFoundDatabase> lostFoundDatabase = Room.databaseBuilder(
                            context.getApplicationContext(),
                            LostFoundDatabase.class,
                            "lost_found_database" // Database name
                    );
                    lostFoundDatabase.fallbackToDestructiveMigration();// Database name
                    instance = lostFoundDatabase // Automatically destroy the database when migrating
                            .build();
                }
            }
        }
        return instance;
    }
}
