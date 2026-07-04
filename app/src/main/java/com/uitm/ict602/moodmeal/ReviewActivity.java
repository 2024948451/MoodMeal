package com.uitm.ict602.moodmeal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ReviewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review); // Matches your XML name

        EditText etPlaceName = findViewById(R.id.etPlaceName);
        EditText etReviewText = findViewById(R.id.etReviewText);
        Button btnCamera = findViewById(R.id.btnCamera);
        Button btnSubmitReview = findViewById(R.id.btnSubmitReview);

        // Open Camera Placeholder
        btnCamera.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Camera...", Toast.LENGTH_SHORT).show();
            // In the future, use: startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 100);
        });

        // Submit Action
        btnSubmitReview.setOnClickListener(v -> {
            String name = etPlaceName.getText().toString();
            String review = etReviewText.getText().toString();

            if (name.isEmpty() || review.isEmpty()) {
                Toast.makeText(this, "Please fill in all details", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Review Submitted!", Toast.LENGTH_SHORT).show();
                finish(); // Go back to previous screen
            }
        });
    }
}