package com.uitm.ict602.moodmeal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class FavouriteActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        setupActions();
    }

    private void setupActions() {
        setClickIfExists("btnBackFavourite", () -> finish());

        setClickIfExists("btnOpenMap", () ->
                startActivity(new Intent(this, MapActivity.class)));

        setClickIfExists("btnOpenReview", () ->
                startActivity(new Intent(this, ReviewActivity.class)));

        setClickIfExists("btnRemoveFavourite", () ->
                Toast.makeText(this, "Favourite removed.", Toast.LENGTH_SHORT).show());

        setClickIfExists("navHome", () ->
                startActivity(new Intent(this, HomeActivity.class)));

        setClickIfExists("navExplore", () ->
                startActivity(new Intent(this, MapActivity.class)));

        setClickIfExists("navPlus", () ->
                startActivity(new Intent(this, ReviewActivity.class)));

        setClickIfExists("navFav", () ->
                Toast.makeText(this, "You are already on Favourites.", Toast.LENGTH_SHORT).show());

        setClickIfExists("navProfile", () ->
                startActivity(new Intent(this, ManageAccountActivity.class)));
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