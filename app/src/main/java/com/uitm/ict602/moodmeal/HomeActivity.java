package com.uitm.ict602.moodmeal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView tvGreeting = findViewById(R.id.tvGreetingHome);
        tvGreeting.setText("Hey, " + MoodMealPrefs.getFirstName(this) + " 👋");

        findViewById(R.id.btnSpinNow).setOnClickListener(v -> openMood());
        findViewById(R.id.cardMoodSelection).setOnClickListener(v -> openMood());

        findViewById(R.id.cardNearbyFood).setOnClickListener(v ->
                toast("Nearby Food page is not included in this 6-page prototype."));

        findViewById(R.id.cardFavourites).setOnClickListener(v ->
                toast("Favourites page is not included in this 6-page prototype."));

        findViewById(R.id.cardUploadReview).setOnClickListener(v ->
                toast("Camera upload review needs separate camera/storage module later."));

        findViewById(R.id.cardReviews).setOnClickListener(v ->
                toast("Reviews page is not included in this 6-page prototype."));

        findViewById(R.id.cardSettings).setOnClickListener(v ->
                startActivity(new Intent(this, ManageAccountActivity.class)));

        setupBottomNav();
    }

    private void setupBottomNav() {
        findViewById(R.id.navHome).setOnClickListener(v ->
                toast("You are already on Home."));

        findViewById(R.id.navExplore).setOnClickListener(v ->
                toast("Explore page is not included in this 6-page prototype."));

        findViewById(R.id.navPlus).setOnClickListener(v ->
                toast("Upload action placeholder. Add camera feature later."));

        findViewById(R.id.navFav).setOnClickListener(v ->
                toast("Favourites page is not included in this 6-page prototype."));

        findViewById(R.id.navProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ManageAccountActivity.class)));
    }

    private void openMood() {
        startActivity(new Intent(this, MoodSelectionActivity.class));
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}