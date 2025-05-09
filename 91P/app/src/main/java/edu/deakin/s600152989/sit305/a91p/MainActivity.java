package edu.deakin.s600152989.sit305.a91p;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    private final int FINE_PERMISSION_CODE=1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        // Setup Zoom In and Zoom Out buttons
        Button zoomInButton = findViewById(R.id.zoom_in_button);
        Button zoomOutButton = findViewById(R.id.zoom_out_button);

        zoomInButton.setOnClickListener(v -> {
            if (myMap != null) {
                float currentZoom = myMap.getCameraPosition().zoom;
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myMap.getCameraPosition().target, currentZoom + 1));  // Zoom in by 1 level
            }
        });

        zoomOutButton.setOnClickListener(v -> {
            if (myMap != null) {
                float currentZoom = myMap.getCameraPosition().zoom;
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myMap.getCameraPosition().target, currentZoom - 1));  // Zoom out by 1 level
            }
        });

        // Set up the button to go back to Deakin
        Button buttonGoToDeakin = findViewById(R.id.buttonGoToDeakin);
        buttonGoToDeakin.setOnClickListener(v -> goToDeakin());

    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    if (mapFragment != null) {
                        mapFragment.getMapAsync(MainActivity.this);
                    }
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        myMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            myMap.setMyLocationEnabled(true);
        }
        myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        LatLng deakin = new LatLng(-37.847565, 144);
        myMap.addMarker(new MarkerOptions().position(deakin).title("Deakin").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

        LatLng sydney = new LatLng(-33.8708, 151.2);
        // Add a marker for Deakin University (Initial Position)

        myMap.addMarker(new MarkerOptions()
                .position(deakin)
                .title("Deakin")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));


        // Set a fallback location to Deakin (default zoom level)
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deakin, 15));

        if (currentLocation != null) {
            updateMapLocation(currentLocation);
        } else {
                // Fallback to Sydney
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10));
        }


    }

    private void updateMapLocation(Location location) {
        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        myMap.addMarker(new MarkerOptions()
                .position(userLatLng)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);
        if(requestCode == FINE_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission is denied, please allow the permission", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void goToDeakin() {
        if (myMap != null) {
            LatLng deakin = new LatLng(-38.197789, 144.115265);  // Coordinates for Deakin University
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deakin, 15));  // Zoom to Deakin with zoom level 15
            myMap.addMarker(new MarkerOptions()
                    .position(deakin)
                    .title("Deakin University")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
        }
    }

}