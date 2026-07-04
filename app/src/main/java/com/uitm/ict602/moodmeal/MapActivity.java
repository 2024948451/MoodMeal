package com.uitm.ict602.moodmeal;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

public class MapActivity extends Activity {

    private static final int REQUEST_LOCATION_PERMISSION = 300;

    private TextView tvLocationStatus;

    private double currentLat = 3.0738;
    private double currentLng = 101.5183;
    private boolean locationDetected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        tvLocationStatus = findViewById(R.id.tvLocationStatus);

        findViewById(R.id.btnBackMap).setOnClickListener(v -> finish());

        findViewById(R.id.btnGetLocation).setOnClickListener(v -> checkLocationPermissionAndDetect());

        findViewById(R.id.btnOpenGoogleMaps).setOnClickListener(v -> openNearbyFoodMap());
    }

    private void checkLocationPermissionAndDetect() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    REQUEST_LOCATION_PERMISSION
            );

        } else {
            detectLocation();
        }
    }

    private void detectLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager == null) {
            Toast.makeText(this, "Location service not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gpsEnabled && !networkEnabled) {
            Toast.makeText(this, "Please enable GPS/location first.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return;
        }

        Location location = null;

        try {
            if (gpsEnabled) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (location == null && networkEnabled) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (location != null) {
                currentLat = location.getLatitude();
                currentLng = location.getLongitude();
                locationDetected = true;

                tvLocationStatus.setText(
                        "Location detected!\n\nLatitude: " + currentLat +
                                "\nLongitude: " + currentLng +
                                "\n\nNearby suggestions are ready."
                );

                Toast.makeText(this, "Location detected.", Toast.LENGTH_SHORT).show();

            } else {
                locationDetected = false;

                tvLocationStatus.setText(
                        "GPS is enabled, but no fresh location is available yet.\n\n" +
                                "Using default campus area for demo.\n\n" +
                                "Tap Open Nearby Food in Google Maps."
                );

                Toast.makeText(this, "Using fallback location for demo.", Toast.LENGTH_LONG).show();
            }

        } catch (SecurityException e) {
            Toast.makeText(this, "Location permission error.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openNearbyFoodMap() {
        String query = "food near " + currentLat + "," + currentLng;

        Uri uri = Uri.parse("geo:" + currentLat + "," + currentLng + "?q=" + Uri.encode(query));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Uri browserUri = Uri.parse(
                    "https://www.google.com/maps/search/?api=1&query=" +
                            Uri.encode("food near " + currentLat + "," + currentLng)
            );

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, browserUri);
            startActivity(browserIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                detectLocation();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}