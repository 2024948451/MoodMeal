package com.uitm.ict602.moodmeal;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map); // Matches your XML name

        // Initialize Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Setup Get Directions Button
        Button btnGetDirections = findViewById(R.id.btnGetDirections);
        btnGetDirections.setOnClickListener(v -> {
            Toast.makeText(this, "Opening directions...", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Dummy Location (UiTM Shah Alam for example)
        LatLng restaurantLocation = new LatLng(3.0697, 101.5037);
        mMap.addMarker(new MarkerOptions().position(restaurantLocation).title("Campus Café").snippet("RM8-RM15 | Rating: 4.5"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurantLocation, 15));
    }
}