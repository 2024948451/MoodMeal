package com.uitm.ict602.moodmeal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RecommendationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation); // Matches your XML name

        // Setup Buttons
        Button btnViewMap = findViewById(R.id.btnViewMap);
        Button btnSaveFavourite = findViewById(R.id.btnSaveFavourite);

        // View on Map Action
        btnViewMap.setOnClickListener(v -> {
            Intent intent = new Intent(RecommendationActivity.this, MapActivity.class);
            startActivity(intent);
        });

        // Save Favourite Action
        btnSaveFavourite.setOnClickListener(v -> {
            Toast.makeText(this, "Food saved to Favourites!", Toast.LENGTH_SHORT).show();
        });
    }
}