package com.uitm.ict602.moodmeal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ManageAccountActivity extends Activity {

    private TextView tvName;
    private TextView tvEmail;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        // Initialize Firebase services
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        initializeViews();
        initializeClickListeners();
        refreshProfile();
        setupBottomNav();
    }

    private void initializeViews() {
        tvName = findViewById(R.id.tvAccountName);
        tvEmail = findViewById(R.id.tvAccountEmail);
    }

    private void initializeClickListeners() {
        findViewById(R.id.tvBackManage)
                .setOnClickListener(v -> finish());

        findViewById(R.id.btnUpdateProfile)
                .setOnClickListener(v -> showUpdateProfileDialog());

        findViewById(R.id.btnResetPassword)
                .setOnClickListener(v -> showResetPasswordDialog());

        findViewById(R.id.btnDeleteAccount)
                .setOnClickListener(v -> showDeleteAccountDialog());

        findViewById(R.id.btnLogout)
                .setOnClickListener(v -> logout());
    }

    private void refreshProfile() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            openLoginActivity();
            return;
        }

        String defaultName = user.getDisplayName();
        String defaultEmail = user.getEmail();

        tvName.setText(
                defaultName == null || defaultName.isEmpty()
                        ? "MoodMeal User"
                        : defaultName
        );

        tvEmail.setText(
                defaultEmail == null
                        ? ""
                        : defaultEmail
        );

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        return;
                    }

                    String fullName =
                            document.getString("fullName");

                    String email =
                            document.getString("email");

                    if (fullName != null && !fullName.isEmpty()) {
                        tvName.setText(fullName);
                    }

                    if (email != null && !email.isEmpty()) {
                        tvEmail.setText(email);
                    }
                })
                .addOnFailureListener(exception ->
                        Toast.makeText(
                                ManageAccountActivity.this,
                                "Unable to load profile information.",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void showUpdateProfileDialog() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            openLoginActivity();
            return;
        }

        LinearLayout layout = createDialogLayout();

        EditText nameInput = new EditText(this);
        nameInput.setHint("Full name");
        nameInput.setText(tvName.getText().toString());
        layout.addView(nameInput);

        EditText emailInput = new EditText(this);
        emailInput.setHint("Email address");
        emailInput.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );
        emailInput.setText(tvEmail.getText().toString());
        layout.addView(emailInput);

        EditText passwordInput = new EditText(this);
        passwordInput.setHint(
                "Current password—required only when changing email"
        );
        passwordInput.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD
        );
        layout.addView(passwordInput);

        new AlertDialog.Builder(this)
                .setTitle("Update Profile")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String fullName =
                            nameInput.getText().toString().trim();

                    String newEmail =
                            emailInput.getText().toString().trim();

                    String currentPassword =
                            passwordInput.getText().toString();

                    if (fullName.isEmpty()) {
                        toast("Full name is required.");
                        return;
                    }

                    if (!Patterns.EMAIL_ADDRESS
                            .matcher(newEmail)
                            .matches()) {
                        toast("Enter a valid email address.");
                        return;
                    }

                    String currentEmail = user.getEmail();

                    boolean emailChanged =
                            currentEmail != null
                                    && !currentEmail.equalsIgnoreCase(
                                    newEmail
                            );

                    if (emailChanged) {
                        if (currentPassword.isEmpty()) {
                            toast(
                                    "Enter your current password to change your email."
                            );
                            return;
                        }

                        reauthenticateUser(
                                user,
                                currentPassword,
                                () -> updateEmail(
                                        user,
                                        fullName,
                                        newEmail
                                )
                        );
                    } else {
                        updateProfileData(
                                user,
                                fullName,
                                newEmail
                        );
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateEmail(
            FirebaseUser user,
            String fullName,
            String newEmail
    ) {
        user.updateEmail(newEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updateProfileData(
                                user,
                                fullName,
                                newEmail
                        );
                    } else {
                        showFirebaseError(
                                "Email could not be updated.",
                                task.getException()
                        );
                    }
                });
    }

    private void updateProfileData(
            FirebaseUser user,
            String fullName,
            String email
    ) {
        UserProfileChangeRequest profileUpdate =
                new UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .build();

        user.updateProfile(profileUpdate)
                .addOnCompleteListener(profileTask -> {
                    if (!profileTask.isSuccessful()) {
                        showFirebaseError(
                                "Profile name could not be updated.",
                                profileTask.getException()
                        );
                        return;
                    }

                    Map<String, Object> profileData =
                            new HashMap<>();

                    profileData.put("uid", user.getUid());
                    profileData.put("fullName", fullName);
                    profileData.put("email", email);
                    profileData.put(
                            "updatedAt",
                            FieldValue.serverTimestamp()
                    );

                    db.collection("users")
                            .document(user.getUid())
                            .set(
                                    profileData,
                                    SetOptions.merge()
                            )
                            .addOnSuccessListener(unused -> {
                                tvName.setText(fullName);
                                tvEmail.setText(email);

                                toast(
                                        "Profile updated successfully."
                                );
                            })
                            .addOnFailureListener(exception ->
                                    showFirebaseError(
                                            "Profile could not be saved.",
                                            exception
                                    )
                            );
                });
    }

    private void showResetPasswordDialog() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            openLoginActivity();
            return;
        }

        LinearLayout layout = createDialogLayout();

        EditText currentPasswordInput = new EditText(this);
        currentPasswordInput.setHint("Current password");
        currentPasswordInput.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD
        );
        layout.addView(currentPasswordInput);

        EditText newPasswordInput = new EditText(this);
        newPasswordInput.setHint("New password");
        newPasswordInput.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD
        );
        layout.addView(newPasswordInput);

        EditText confirmPasswordInput = new EditText(this);
        confirmPasswordInput.setHint("Confirm new password");
        confirmPasswordInput.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD
        );
        layout.addView(confirmPasswordInput);

        new AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String currentPassword =
                            currentPasswordInput
                                    .getText()
                                    .toString();

                    String newPassword =
                            newPasswordInput
                                    .getText()
                                    .toString();

                    String confirmPassword =
                            confirmPasswordInput
                                    .getText()
                                    .toString();

                    if (currentPassword.isEmpty()) {
                        toast("Enter your current password.");
                        return;
                    }

                    if (newPassword.length() < 6) {
                        toast(
                                "New password must be at least 6 characters."
                        );
                        return;
                    }

                    if (!newPassword.equals(confirmPassword)) {
                        toast("New passwords do not match.");
                        return;
                    }

                    reauthenticateUser(
                            user,
                            currentPassword,
                            () -> updatePassword(
                                    user,
                                    newPassword
                            )
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updatePassword(
            FirebaseUser user,
            String newPassword
    ) {
        user.updatePassword(newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        toast("Password updated successfully.");
                    } else {
                        showFirebaseError(
                                "Password could not be updated.",
                                task.getException()
                        );
                    }
                });
    }

    private void showDeleteAccountDialog() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            openLoginActivity();
            return;
        }

        LinearLayout layout = createDialogLayout();

        EditText passwordInput = new EditText(this);
        passwordInput.setHint("Enter your current password");
        passwordInput.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD
        );
        layout.addView(passwordInput);

        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage(
                        "This permanently deletes your MoodMeal account, profile information and profile image."
                )
                .setView(layout)
                .setPositiveButton("Delete", (dialog, which) -> {
                    String password =
                            passwordInput.getText().toString();

                    if (password.isEmpty()) {
                        toast(
                                "Enter your current password to continue."
                        );
                        return;
                    }

                    reauthenticateUser(
                            user,
                            password,
                            () -> deleteAccount(user)
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccount(FirebaseUser user) {
        String uid = user.getUid();

        StorageReference profileImageReference =
                storage.getReference()
                        .child("profile_images")
                        .child(uid)
                        .child("profile.jpg");

        /*
         * Continue deleting the account even when there is
         * no uploaded profile image.
         */
        profileImageReference.delete()
                .addOnCompleteListener(storageTask ->
                        deleteFirestoreProfile(user)
                );
    }

    private void deleteFirestoreProfile(FirebaseUser user) {
        db.collection("users")
                .document(user.getUid())
                .delete()
                .addOnCompleteListener(firestoreTask -> {
                    if (!firestoreTask.isSuccessful()) {
                        showFirebaseError(
                                "Profile data could not be deleted.",
                                firestoreTask.getException()
                        );
                        return;
                    }

                    deleteFirebaseUser(user);
                });
    }

    private void deleteFirebaseUser(FirebaseUser user) {
        user.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        toast("Account deleted successfully.");

                        Intent intent = new Intent(
                                ManageAccountActivity.this,
                                RegisterActivity.class
                        );

                        intent.setFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        );

                        startActivity(intent);
                        finish();
                    } else {
                        showFirebaseError(
                                "Account could not be deleted.",
                                task.getException()
                        );
                    }
                });
    }

    private void reauthenticateUser(
            FirebaseUser user,
            String password,
            Runnable successAction
    ) {
        String email = user.getEmail();

        if (email == null || email.isEmpty()) {
            toast(
                    "This action is available for email and password accounts."
            );
            return;
        }

        AuthCredential credential =
                EmailAuthProvider.getCredential(
                        email,
                        password
                );

        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        successAction.run();
                    } else {
                        showFirebaseError(
                                "Current password is incorrect.",
                                task.getException()
                        );
                    }
                });
    }

    private void logout() {
        mAuth.signOut();
        toast("Logged out.");

        openLoginActivity();
    }

    private void setupBottomNav() {
        findViewById(R.id.navHome)
                .setOnClickListener(v -> {
                    startActivity(
                            new Intent(
                                    ManageAccountActivity.this,
                                    HomeActivity.class
                            )
                    );

                    finish();
                });

        findViewById(R.id.navExplore)
                .setOnClickListener(v ->
                        openActivity(MapActivity.class)
                );

        findViewById(R.id.navPlus)
                .setOnClickListener(v ->
                        openActivity(ReviewActivity.class)
                );

        findViewById(R.id.navFav)
                .setOnClickListener(v ->
                        openActivity(FavouriteActivity.class)
                );

        findViewById(R.id.navProfile)
                .setOnClickListener(v ->
                        toast("You are already on Profile.")
                );
    }

    private LinearLayout createDialogLayout() {
        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        int padding = dp(20);

        layout.setPadding(
                padding,
                padding,
                padding,
                padding
        );

        return layout;
    }

    private void showFirebaseError(
            String defaultMessage,
            Exception exception
    ) {
        String message = defaultMessage;

        if (exception
                instanceof FirebaseAuthInvalidCredentialsException) {
            message = "Current password is incorrect.";

        } else if (exception
                instanceof FirebaseAuthUserCollisionException) {
            message =
                    "This email address is already used by another account.";

        } else if (exception
                instanceof FirebaseAuthRecentLoginRequiredException) {
            message =
                    "Please log out, log in again and retry this action.";
        }

        Toast.makeText(
                ManageAccountActivity.this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    private void openLoginActivity() {
        Intent intent = new Intent(
                ManageAccountActivity.this,
                LoginActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }

    private void openActivity(
            Class<?> activityClass
    ) {
        startActivity(
                new Intent(
                        ManageAccountActivity.this,
                        activityClass
                )
        );
    }

    private int dp(int value) {
        return (int) (
                value
                        * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    private void toast(String message) {
        Toast.makeText(
                ManageAccountActivity.this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }
}