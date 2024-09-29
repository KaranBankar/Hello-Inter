package com.example.hellointern;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.content.SharedPreferences;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference databaseReference;

    private RecyclerView recyclerView;
    private Timer locationTimer; // Timer for location updates
    ExtendedFloatingActionButton logout;
    SharedPreferences preferences;
    SharedPreferences.Editor  editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logout=findViewById(R.id.logout);
        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("locations");

        // Initialize Fused Location Provider
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginActivity loginActivity = new LoginActivity();
                loginActivity.saveData(MainActivity.this, false);
                Intent i=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });


        // Check for location permission
        checkLocationPermission();

        // Create Notification Channel
        createNotificationChannel();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            startLocationUpdates(); // Start location updates if permission is granted
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Location permission is required to use this app.", Toast.LENGTH_LONG).show();
        }

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates(); // Start location updates if permission is granted
            } else {
                Toast.makeText(this, "Permission denied! You cannot use the app without enabling location.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    private void startLocationUpdates() {
        // Start a timer to fetch location every 5 minutes
        locationTimer = new Timer();
        locationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                fetchLocation();
            }
        }, 0, 5 * 60 * 1000); // 5 minutes in milliseconds
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String address = getAddressFromLocation(latitude, longitude); // Get address from coordinates
                    storeLocationInFirebase(latitude, longitude, address);
                    showNotification(latitude, longitude, address); // Show notification
                } else {
                    Toast.makeText(this, "Location not found.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0); // Get the first address line
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown Address"; // Default address if unable to get one
    }

    private void storeLocationInFirebase(double latitude, double longitude, String address) {
        String userId = "example_user_id"; // Replace this with the actual user ID or unique identifier
        HashMap<String, Object> locationData = new HashMap<>();
        locationData.put("latitude", latitude);
        locationData.put("longitude", longitude);
        locationData.put("address", address); // Save address as well

        // Use push() to store multiple entries without overriding
        databaseReference.push().setValue(locationData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Location saved!", Toast.LENGTH_SHORT).show();
                        loadLocations(); // Load locations to RecyclerView
                    } else {
                        Toast.makeText(this, "Failed to save location.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showNotification(double latitude, double longitude, String address) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "location_channel")
                .setSmallIcon(R.drawable.splash) // Use your app's icon
                .setContentTitle("Location Update")
                .setContentText("Latitude: " + latitude + ", Longitude: " + longitude + "\nAddress: " + address)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
            return;
        }
        notificationManager.notify(1001, builder.build()); // Notification ID can be any unique number
    }

    private void loadLocations() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<LocationModel> locations = new ArrayList<>();
                for (DataSnapshot locationSnapshot : snapshot.getChildren()) {
                    double latitude = locationSnapshot.child("latitude").getValue(Double.class);
                    double longitude = locationSnapshot.child("longitude").getValue(Double.class);
                    String address = locationSnapshot.child("address").getValue(String.class); // Get the address
                    locations.add(new LocationModel(latitude, longitude, address)); // Update LocationModel to include address
                }
                LocationAdapter adapter = new LocationAdapter(locations);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load locations.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel if necessary
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Location Updates";
            String description = "Channel for location update notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("location_channel", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationTimer != null) {
            locationTimer.cancel(); // Stop the timer when the activity is destroyed
        }
    }
}
