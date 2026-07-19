package com.uitm.ict602.moodmeal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FavouriteActivity extends Activity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String favouriteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        favouriteId = getIntent().getStringExtra("favouriteId");

        // Also accept restaurantId as an alternative extra name.
        if (favouriteId == null || favouriteId.trim().isEmpty()) {
            favouriteId = getIntent().getStringExtra("restaurantId");
        }

        setupActions();
        loadFavourite();
    }

    private void setupActions() {
        setClickIfExists(
                "btnBackFavourite",
                this::finish
        );

        setClickIfExists(
                "btnOpenMap",
                () -> openActivity(MapActivity.class)
        );

        setClickIfExists(
                "btnOpenReview",
                () -> openActivity(ReviewActivity.class)
        );

        setClickIfExists(
                "btnRemoveFavourite",
                this::showRemoveFavouriteDialog
        );

        setClickIfExists(
                "navHome",
                () -> {
                    startActivity(
                            new Intent(
                                    FavouriteActivity.this,
                                    HomeActivity.class
                            )
                    );

                    finish();
                }
        );

        setClickIfExists(
                "navExplore",
                () -> openActivity(MapActivity.class)
        );

        setClickIfExists(
                "navPlus",
                () -> openActivity(ReviewActivity.class)
        );

        setClickIfExists(
                "navFav",
                () -> toast(
                        "You are already on Favourites."
                )
        );

        setClickIfExists(
                "navProfile",
                () -> openActivity(
                        ManageAccountActivity.class
                )
        );
    }

    private void loadFavourite() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            openLoginActivity();
            return;
        }

        if (favouriteId == null || favouriteId.trim().isEmpty()) {
            toast("No favourite was selected.");
            return;
        }

        DocumentReference favouriteReference =
                db.collection("users")
                        .document(currentUser.getUid())
                        .collection("favourites")
                        .document(favouriteId);

        favouriteReference.get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        toast("Favourite was not found.");
                        return;
                    }

                    String name =
                            document.getString("name");

                    String category =
                            document.getString("category");

                    String address =
                            document.getString("address");

                    setTextIfExists(
                            "tvFavouriteName",
                            name
                    );

                    setTextIfExists(
                            "tvFavouriteCategory",
                            category
                    );

                    setTextIfExists(
                            "tvFavouriteAddress",
                            address
                    );
                })
                .addOnFailureListener(exception ->
                        toast("Unable to load favourite.")
                );
    }

    private void showRemoveFavouriteDialog() {
        if (favouriteId == null || favouriteId.trim().isEmpty()) {
            toast("No favourite was selected.");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Remove Favourite")
                .setMessage(
                        "Do you want to remove this place from your favourites?"
                )
                .setPositiveButton(
                        "Remove",
                        (dialog, which) -> removeFavourite()
                )
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void removeFavourite() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            openLoginActivity();
            return;
        }

        db.collection("users")
                .document(currentUser.getUid())
                .collection("favourites")
                .document(favouriteId)
                .delete()
                .addOnSuccessListener(unused -> {
                    toast("Favourite removed.");

                    Intent intent = new Intent(
                            FavouriteActivity.this,
                            HomeActivity.class
                    );

                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    );

                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(exception ->
                        toast("Unable to remove favourite.")
                );
    }

    private void setClickIfExists(
            String idName,
            Runnable action
    ) {
        int id = getResources().getIdentifier(
                idName,
                "id",
                getPackageName()
        );

        if (id == 0) {
            return;
        }

        View view = findViewById(id);

        if (view != null) {
            view.setOnClickListener(
                    v -> action.run()
            );
        }
    }

    private void setTextIfExists(
            String idName,
            String value
    ) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }

        int id = getResources().getIdentifier(
                idName,
                "id",
                getPackageName()
        );

        if (id == 0) {
            return;
        }

        View view = findViewById(id);

        if (view instanceof TextView) {
            ((TextView) view).setText(value);
        }
    }

    private void openActivity(
            Class<?> activityClass
    ) {
        startActivity(
                new Intent(
                        FavouriteActivity.this,
                        activityClass
                )
        );
    }

    private void openLoginActivity() {
        Intent intent = new Intent(
                FavouriteActivity.this,
                LoginActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }

    private void toast(String message) {
        Toast.makeText(
                FavouriteActivity.this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }
}