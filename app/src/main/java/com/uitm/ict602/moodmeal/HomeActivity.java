package com.uitm.ict602.moodmeal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
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
        setupMainActions();
        setupBottomNav();
    }

    private void setupMainActions() {
        findViewById(R.id.tvMenuHome).setOnClickListener(v -> openDrawer());

        findViewById(R.id.btnSpinNow).setOnClickListener(v -> openMood());
        findViewById(R.id.cardMoodSelection).setOnClickListener(v -> openMood());

        findViewById(R.id.cardNearbyFood).setOnClickListener(v ->
                startActivity(new Intent(this, MapActivity.class)));

        findViewById(R.id.cardFavourites).setOnClickListener(v ->
                startActivity(new Intent(this, FavouriteActivity.class)));

        findViewById(R.id.cardReviews).setOnClickListener(v ->
                startActivity(new Intent(this, ReviewActivity.class)));

        findViewById(R.id.cardSettings).setOnClickListener(v ->
                startActivity(new Intent(this, ManageAccountActivity.class)));

        findViewById(R.id.cardSettings).setOnClickListener(v ->
                startActivity(new Intent(this, ManageAccountActivity.class)));
    }

    private void setupDrawer() {
        drawerOverlay.setOnClickListener(v -> closeDrawer());
        findViewById(R.id.btnCloseDrawer).setOnClickListener(v -> closeDrawer());

        findViewById(R.id.drawerMood).setOnClickListener(v -> {
            closeDrawer();
            startActivity(new Intent(this, MoodSelectionActivity.class));
        });

        findViewById(R.id.drawerAccount).setOnClickListener(v -> {
            closeDrawer();
            startActivity(new Intent(this, ManageAccountActivity.class));
        });

        findViewById(R.id.drawerAbout).setOnClickListener(v -> {
            closeDrawer();
            Toast.makeText(
                    this,
                    "MoodMeal helps students decide what to eat based on mood.",
                    Toast.LENGTH_LONG
            ).show();
        });

        findViewById(R.id.drawerLogout).setOnClickListener(v -> {
            MoodMealPrefs.logout(this);

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void openDrawer() {
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

    private void setupBottomNav() {
        findViewById(R.id.navHome).setOnClickListener(v ->
                toast("You are already on Home."));

        findViewById(R.id.navExplore).setOnClickListener(v ->
                toast("Explore page is not included in this 6-page prototype."));

        findViewById(R.id.navPlus).setOnClickListener(v ->
                toast("Upload action placeholder. Add camera feature later."));

        // UPDATE THIS LINE TO OPEN FAVOURITE ACTIVITY
        findViewById(R.id.navFav).setOnClickListener(v ->
                startActivity(new Intent(this, FavouriteActivity.class)));

        findViewById(R.id.navProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ManageAccountActivity.class)));
    }

    private void openMood() {
        startActivity(new Intent(this, MoodSelectionActivity.class));
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}