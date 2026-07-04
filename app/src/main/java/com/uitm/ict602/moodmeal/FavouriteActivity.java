package com.uitm.ict602.moodmeal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class FavouriteActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        // Back Button
        findViewById(R.id.tvBackFav).setOnClickListener(v -> finish());

        // Setup Remove Buttons (Just hiding the cards visually for the prototype)
        findViewById(R.id.btnRemoveFav1).setOnClickListener(v -> {
            ((View) v.getParent()).setVisibility(View.GONE);
            toast("Removed from favourites!");
        });

        findViewById(R.id.btnRemoveFav2).setOnClickListener(v -> {
            ((View) v.getParent()).setVisibility(View.GONE);
            toast("Removed from favourites!");
        });

        setupBottomNav();
    }

    private void setupBottomNav() {
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish(); // Close this page so it doesn't stack
        });

        findViewById(R.id.navExplore).setOnClickListener(v ->
                toast("Explore page is not included in this prototype."));

        findViewById(R.id.navPlus).setOnClickListener(v ->
                toast("Upload action placeholder."));

        findViewById(R.id.navFav).setOnClickListener(v ->
                toast("You are already on Favourites."));

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, ManageAccountActivity.class));
            finish();
        });
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}