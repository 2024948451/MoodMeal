package com.uitm.ict602.moodmeal;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends Activity {

    private EditText etFullName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;

    private CheckBox cbTerms;

    private ImageView ivTogglePassword;
    private ImageView ivToggleConfirmPassword;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private boolean passwordVisible = false;
    private boolean confirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase services
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        initializeTextStyles();
        initializeClickListeners();
    }

    private void initializeViews() {
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        cbTerms = findViewById(R.id.cbTerms);

        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        ivToggleConfirmPassword =
                findViewById(R.id.ivToggleConfirmPassword);
    }

    private void initializeTextStyles() {
        TextView tvTermsText = findViewById(R.id.tvTermsText);
        TextView tvLogin = findViewById(R.id.tvLogin);

        styleTermsText(tvTermsText);
        styleLoginText(tvLogin);
    }

    private void initializeClickListeners() {
        findViewById(R.id.btnBackRegister)
                .setOnClickListener(v -> finish());

        ivTogglePassword.setOnClickListener(
                v -> togglePassword()
        );

        ivToggleConfirmPassword.setOnClickListener(
                v -> toggleConfirmPassword()
        );

        findViewById(R.id.btnAvatarCamera)
                .setOnClickListener(v ->
                        Toast.makeText(
                                RegisterActivity.this,
                                "Profile image upload can be added later.",
                                Toast.LENGTH_SHORT
                        ).show()
                );

        findViewById(R.id.btnCreateAccount)
                .setOnClickListener(v -> registerUser());

        findViewById(R.id.btnGoogle)
                .setOnClickListener(v ->
                        Toast.makeText(
                                RegisterActivity.this,
                                "Google sign-up is currently unavailable.",
                                Toast.LENGTH_SHORT
                        ).show()
                );

        findViewById(R.id.btnEmail)
                .setOnClickListener(v ->
                        Toast.makeText(
                                RegisterActivity.this,
                                "Email sign-up selected.",
                                Toast.LENGTH_SHORT
                        ).show()
                );

        findViewById(R.id.tvLogin)
                .setOnClickListener(v -> openLoginActivity());
    }

    private void styleTermsText(TextView tvTermsText) {
        String text =
                "I agree to the Terms of Service and Privacy Policy";

        SpannableString spannable = new SpannableString(text);

        applyTextColor(
                spannable,
                text,
                "Terms of Service"
        );

        applyTextColor(
                spannable,
                text,
                "Privacy Policy"
        );

        tvTermsText.setText(spannable);
    }

    private void styleLoginText(TextView tvLogin) {
        String text = "Already have an account? Log in";
        SpannableString spannable = new SpannableString(text);

        applyTextColor(
                spannable,
                text,
                "Log in"
        );

        tvLogin.setText(spannable);
    }

    private void applyTextColor(
            SpannableString spannable,
            String fullText,
            String targetText
    ) {
        int start = fullText.indexOf(targetText);

        if (start >= 0) {
            int end = start + targetText.length();

            spannable.setSpan(
                    new ForegroundColorSpan(
                            Color.parseColor("#009B93")
                    ),
                    start,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
    }

    private void togglePassword() {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            etPassword.setTransformationMethod(
                    HideReturnsTransformationMethod.getInstance()
            );
        } else {
            etPassword.setTransformationMethod(
                    PasswordTransformationMethod.getInstance()
            );
        }

        etPassword.setSelection(
                etPassword.getText().length()
        );
    }

    private void toggleConfirmPassword() {
        confirmPasswordVisible = !confirmPasswordVisible;

        if (confirmPasswordVisible) {
            etConfirmPassword.setTransformationMethod(
                    HideReturnsTransformationMethod.getInstance()
            );
        } else {
            etConfirmPassword.setTransformationMethod(
                    PasswordTransformationMethod.getInstance()
            );
        }

        etConfirmPassword.setSelection(
                etConfirmPassword.getText().length()
        );
    }

    private void registerUser() {
        String fullName =
                etFullName.getText().toString().trim();

        String email =
                etEmail.getText().toString().trim();

        String password =
                etPassword.getText().toString();

        String confirmPassword =
                etConfirmPassword.getText().toString();

        if (!validateInput(
                fullName,
                email,
                password,
                confirmPassword
        )) {
            return;
        }

        createFirebaseAccount(
                fullName,
                email,
                password
        );
    }

    private boolean validateInput(
            String fullName,
            String email,
            String password,
            String confirmPassword
    ) {
        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError(
                    "Password must be at least 6 characters"
            );

            etPassword.requestFocus();
            return false;
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError(
                    "Please confirm your password"
            );

            etConfirmPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError(
                    "Passwords do not match"
            );

            etConfirmPassword.requestFocus();
            return false;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(
                    RegisterActivity.this,
                    "Please agree to the terms first.",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        return true;
    }

    private void createFirebaseAccount(
            String fullName,
            String email,
            String password
    ) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        showRegistrationError(
                                task.getException()
                        );

                        return;
                    }

                    FirebaseUser currentUser =
                            mAuth.getCurrentUser();

                    if (currentUser == null) {
                        Toast.makeText(
                                RegisterActivity.this,
                                "Account created, but user data could not be loaded.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    updateFirebaseProfile(
                            currentUser,
                            fullName,
                            email
                    );
                });
    }

    private void updateFirebaseProfile(
            FirebaseUser user,
            String fullName,
            String email
    ) {
        UserProfileChangeRequest profileUpdate =
                new UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .build();

        user.updateProfile(profileUpdate)
                .addOnCompleteListener(task ->
                        saveUserToFirestore(
                                user,
                                fullName,
                                email
                        )
                );
    }

    private void saveUserToFirestore(
            FirebaseUser user,
            String fullName,
            String email
    ) {
        Map<String, Object> userData = new HashMap<>();

        userData.put("uid", user.getUid());
        userData.put("fullName", fullName);
        userData.put("email", email);
        userData.put("profileImageUrl", "");
        userData.put("createdAt", FieldValue.serverTimestamp());

        db.collection("users")
                .document(user.getUid())
                .set(userData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(
                            RegisterActivity.this,
                            "Account created successfully!",
                            Toast.LENGTH_SHORT
                    ).show();

                    mAuth.signOut();
                    openLoginActivity();
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(
                            RegisterActivity.this,
                            "Account created, but profile data could not be saved: "
                                    + exception.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void showRegistrationError(Exception exception) {
        String errorMessage =
                "Account registration failed.";

        if (exception != null
                && exception.getMessage() != null) {
            errorMessage = exception.getMessage();
        }

        Toast.makeText(
                RegisterActivity.this,
                errorMessage,
                Toast.LENGTH_LONG
        ).show();
    }

    private void openLoginActivity() {
        Intent intent = new Intent(
                RegisterActivity.this,
                LoginActivity.class
        );

        startActivity(intent);
        finish();
    }
}