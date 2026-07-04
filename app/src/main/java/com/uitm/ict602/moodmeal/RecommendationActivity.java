package com.uitm.ict602.moodmeal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class RecommendationActivity extends Activity {

    private String selectedMood;
    private String recommendationMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        selectedMood = getIntent().getStringExtra("selectedMood");
        recommendationMessage = getIntent().getStringExtra("recommendationMessage");

        if (selectedMood == null || selectedMood.trim().isEmpty()) {
            selectedMood = "Mood";
        }

        if (recommendationMessage == null || recommendationMessage.trim().isEmpty()) {
            recommendationMessage = "Choose a mood to view recommendation.";
        }

        bindTextIfExists("tvRecommendationTitle", "Recommendation for " + selectedMood);
        bindTextIfExists("tvSelectedMood", selectedMood);
        bindTextIfExists("tvRecommendationMessage", recommendationMessage);

        setupActions();
    }

    private void setupActions() {
        setClickIfExists("btnBackRecommendation", () -> finish());

        setClickIfExists("btnOpenMap", () ->
                startActivity(new Intent(this, MapActivity.class)));

        setClickIfExists("btnSaveFavourite", () -> {
            Toast.makeText(this, "Saved to favourites.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, FavouriteActivity.class));
        });

        setClickIfExists("btnUploadReview", () ->
                startActivity(new Intent(this, ReviewActivity.class)));

        setClickIfExists("navHome", () ->
                startActivity(new Intent(this, HomeActivity.class)));

        setClickIfExists("navExplore", () ->
                startActivity(new Intent(this, MapActivity.class)));

        setClickIfExists("navPlus", () ->
                startActivity(new Intent(this, ReviewActivity.class)));

        setClickIfExists("navFav", () ->
                startActivity(new Intent(this, FavouriteActivity.class)));

        setClickIfExists("navProfile", () ->
                startActivity(new Intent(this, ManageAccountActivity.class)));
    }

    private void bindTextIfExists(String idName, String text) {
        int id = getResources().getIdentifier(idName, "id", getPackageName());

        if (id != 0) {
            TextView textView = findViewById(id);
            if (textView != null) {
                textView.setText(text);
            }
        }
    }

    private void setClickIfExists(String idName, Runnable action) {
        int id = getResources().getIdentifier(idName, "id", getPackageName());

        if (id != 0) {
            android.view.View view = findViewById(id);
            if (view != null) {
                view.setOnClickListener(v -> action.run());
            }
        }
    }
}