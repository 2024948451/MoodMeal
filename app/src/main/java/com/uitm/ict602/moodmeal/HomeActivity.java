package com.uitm.ict602.moodmeal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

    private View drawerOverlay;
    private View drawerPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView tvGreeting = findViewById(R.id.tvGreetingHome);
        tvGreeting.setText("Hey, " + MoodMealPrefs.getFirstName(this) + " 👋");

        drawerOverlay = findViewById(R.id.drawerOverlay);
        drawerPanel = findViewById(R.id.drawerPanel);

        setupDrawer();
        setupHomeCards();
        setupBottomNav();
    }

    private void setupHomeCards() {
        findViewById(R.id.tvMenuHome).setOnClickListener(v -> openDrawer());

        findViewById(R.id.btnSpinNow).setOnClickListener(v ->
                openActivity(MoodSelectionActivity.class));

        findViewById(R.id.cardMoodSelection).setOnClickListener(v ->
                openActivity(MoodSelectionActivity.class));

        findViewById(R.id.cardNearbyFood).setOnClickListener(v ->
                openActivity(MapActivity.class));

        findViewById(R.id.cardFavourites).setOnClickListener(v ->
                openActivity(FavouriteActivity.class));

        findViewById(R.id.cardUploadReview).setOnClickListener(v ->
                openActivity(ReviewActivity.class));

        findViewById(R.id.cardReviews).setOnClickListener(v ->
                openActivity(RecommendationActivity.class));

        findViewById(R.id.cardSettings).setOnClickListener(v ->
                openActivity(ManageAccountActivity.class));
    }

    private void setupDrawer() {
        if (drawerOverlay == null || drawerPanel == null) {
            return;
        }

        drawerOverlay.setOnClickListener(v -> closeDrawer());

        View closeButton = findViewById(R.id.btnCloseDrawer);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> closeDrawer());
        }

        View drawerMood = findViewById(R.id.drawerMood);
        if (drawerMood != null) {
            drawerMood.setOnClickListener(v -> {
                closeDrawer();
                openActivity(MoodSelectionActivity.class);
            });
        }

        View drawerAccount = findViewById(R.id.drawerAccount);
        if (drawerAccount != null) {
            drawerAccount.setOnClickListener(v -> {
                closeDrawer();
                openActivity(ManageAccountActivity.class);
            });
        }

        View drawerAbout = findViewById(R.id.drawerAbout);
        if (drawerAbout != null) {
            drawerAbout.setOnClickListener(v -> {
                closeDrawer();
                toast("MoodMeal helps students decide what to eat based on mood.");
            });
        }

        View drawerLogout = findViewById(R.id.drawerLogout);
        if (drawerLogout != null) {
            drawerLogout.setOnClickListener(v -> logoutUser());
        }
    }

    private void setupBottomNav() {
        findViewById(R.id.navHome).setOnClickListener(v ->
                toast("You are already on Home."));

        findViewById(R.id.navExplore).setOnClickListener(v ->
                openActivity(MapActivity.class));

        findViewById(R.id.navPlus).setOnClickListener(v ->
                openActivity(ReviewActivity.class));

        findViewById(R.id.navFav).setOnClickListener(v ->
                openActivity(FavouriteActivity.class));

        findViewById(R.id.navProfile).setOnClickListener(v ->
                openActivity(ManageAccountActivity.class));
    }

    private void openDrawer() {
        if (drawerOverlay == null || drawerPanel == null) {
            toast("Menu is not available.");
            return;
        }

        drawerOverlay.setAlpha(0f);
        drawerOverlay.setVisibility(View.VISIBLE);

        drawerOverlay.animate()
                .alpha(1f)
                .setDuration(180)
                .start();

        drawerPanel.animate()
                .translationX(0f)
                .setDuration(230)
                .start();
    }

    private void closeDrawer() {
        if (drawerOverlay == null || drawerPanel == null) {
            return;
        }

        drawerOverlay.animate()
                .alpha(0f)
                .setDuration(180)
                .withEndAction(() -> drawerOverlay.setVisibility(View.GONE))
                .start();

        drawerPanel.animate()
                .translationX(-dp(292))
                .setDuration(230)
                .start();
    }

    private void logoutUser() {
        MoodMealPrefs.logout(this);

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void openActivity(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass));
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}