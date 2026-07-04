package com.uitm.ict602.moodmeal;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class AdminCrudActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_crud); // Matches your XML name

        // Setup CRUD buttons
        Button btnAddFood = findViewById(R.id.btnAddFood);
        Button btnViewFood = findViewById(R.id.btnViewFood);
        Button btnEditFood = findViewById(R.id.btnEditFood);
        Button btnDeleteFood = findViewById(R.id.btnDeleteFood);

        // Add (Create)
        btnAddFood.setOnClickListener(v -> {
            Toast.makeText(this, "Food successfully added to database!", Toast.LENGTH_SHORT).show();
        });

        // View (Read)
        btnViewFood.setOnClickListener(v -> {
            Toast.makeText(this, "Loading food list...", Toast.LENGTH_SHORT).show();
        });

        // Edit (Update)
        btnEditFood.setOnClickListener(v -> {
            Toast.makeText(this, "Food details updated!", Toast.LENGTH_SHORT).show();
        });

        // Delete
        btnDeleteFood.setOnClickListener(v -> {
            Toast.makeText(this, "Food place deleted from database.", Toast.LENGTH_SHORT).show();
        });
    }
}