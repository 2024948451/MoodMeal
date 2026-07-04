package com.uitm.ict602.moodmeal;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ReviewActivity extends Activity {

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_IMAGE_CAPTURE = 201;

    private ImageView ivFoodPreview;
    private EditText etReviewText;
    private EditText etRating;

    private boolean hasImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ivFoodPreview = findViewById(R.id.ivFoodPreview);
        etReviewText = findViewById(R.id.etReviewText);
        etRating = findViewById(R.id.etRating);

        findViewById(R.id.btnBackReview).setOnClickListener(v -> finish());

        findViewById(R.id.btnUploadImage).setOnClickListener(v -> checkCameraPermissionAndOpen());

        findViewById(R.id.btnSubmitReview).setOnClickListener(v -> submitReview());
    }

    private void checkCameraPermissionAndOpen() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION
            );
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "No camera app found on this device.", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitReview() {
        String review = etReviewText.getText().toString().trim();
        String rating = etRating.getText().toString().trim();

        if (!hasImage) {
            Toast.makeText(this, "Please capture a food image first.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (review.isEmpty()) {
            etReviewText.setError("Review is required");
            return;
        }

        if (rating.isEmpty()) {
            etRating.setError("Rating is required");
            return;
        }

        Toast.makeText(this, "Review uploaded successfully!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, RecommendationActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();

            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                if (imageBitmap != null) {
                    ivFoodPreview.setImageBitmap(imageBitmap);
                    hasImage = true;
                    Toast.makeText(this, "Image captured.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}