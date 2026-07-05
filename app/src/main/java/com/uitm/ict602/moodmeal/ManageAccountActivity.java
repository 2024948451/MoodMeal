package com.uitm.ict602.moodmeal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ManageAccountActivity extends Activity {

    private TextView tvName;
    private TextView tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        tvName = findViewById(R.id.tvAccountName);
        tvEmail = findViewById(R.id.tvAccountEmail);

        refreshProfile();

        findViewById(R.id.tvBackManage).setOnClickListener(v -> finish());

        findViewById(R.id.btnUpdateProfile).setOnClickListener(v -> showUpdateProfileDialog());
        findViewById(R.id.btnResetPassword).setOnClickListener(v -> showResetPasswordDialog());
        findViewById(R.id.btnDeleteAccount).setOnClickListener(v -> showDeleteAccountDialog());
        findViewById(R.id.btnLogout).setOnClickListener(v -> logout());

        setupBottomNav();
    }

    private void refreshProfile() {
        tvName.setText(MoodMealPrefs.getName(this));
        tvEmail.setText(MoodMealPrefs.getEmail(this));
    }

    private void showUpdateProfileDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(20);
        layout.setPadding(padding, padding, padding, padding);

        EditText nameInput = new EditText(this);
        nameInput.setHint("Full name");
        nameInput.setText(MoodMealPrefs.getName(this));
        layout.addView(nameInput);

        EditText emailInput = new EditText(this);
        emailInput.setHint("Email");
        emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailInput.setText(MoodMealPrefs.getEmail(this));
        layout.addView(emailInput);

        new AlertDialog.Builder(this)
                .setTitle("Update Profile")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String email = emailInput.getText().toString().trim();

                    if (name.isEmpty() || email.isEmpty() || !email.contains("@")) {
                        Toast.makeText(this, "Invalid name or email", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    MoodMealPrefs.updateProfile(this, name, email);
                    refreshProfile();
                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showResetPasswordDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(20);
        layout.setPadding(padding, padding, padding, padding);

        EditText passwordInput = new EditText(this);
        passwordInput.setHint("New password");
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(passwordInput);

        new AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String password = passwordInput.getText().toString();

                    if (password.length() < 6) {
                        Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    MoodMealPrefs.updatePassword(this, password);
                    Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("This will delete your local MoodMeal account from this device. Continue?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    MoodMealPrefs.deleteAccount(this);
                    Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, RegisterActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logout() {
        MoodMealPrefs.logout(this);
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void setupBottomNav() {
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        findViewById(R.id.navExplore).setOnClickListener(v ->
                openActivity(MapActivity.class));

        findViewById(R.id.navPlus).setOnClickListener(v ->
                openActivity(ReviewActivity.class));

        findViewById(R.id.navFav).setOnClickListener(v ->
                openActivity(FavouriteActivity.class));

        findViewById(R.id.navProfile).setOnClickListener(v ->
                toast("You are already on Profile."));
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