package com.uitm.ict602.moodmeal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class AdminCrudActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_crud);

        setupActions();
    }

    private void setupActions() {
        setClickIfExists("btnBackAdmin", () -> finish());

        setClickIfExists("btnAddFood", () ->
                Toast.makeText(this, "Food record added.", Toast.LENGTH_SHORT).show());

        setClickIfExists("btnUpdateFood", () ->
                Toast.makeText(this, "Food record updated.", Toast.LENGTH_SHORT).show());

        setClickIfExists("btnDeleteFood", () ->
                Toast.makeText(this, "Food record deleted.", Toast.LENGTH_SHORT).show());

        setClickIfExists("btnViewHome", () ->
                startActivity(new Intent(this, HomeActivity.class)));

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