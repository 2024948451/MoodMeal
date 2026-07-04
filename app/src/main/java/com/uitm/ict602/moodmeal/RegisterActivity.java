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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private CheckBox cbTerms;
    private ImageView ivTogglePassword, ivToggleConfirmPassword;
    private boolean passwordVisible = false;
    private boolean confirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbTerms = findViewById(R.id.cbTerms);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        ivToggleConfirmPassword = findViewById(R.id.ivToggleConfirmPassword);

        TextView tvTermsText = findViewById(R.id.tvTermsText);
        TextView tvLogin = findViewById(R.id.tvLogin);

        styleTermsText(tvTermsText);
        styleLoginText(tvLogin);

        findViewById(R.id.btnBackRegister).setOnClickListener(v -> finish());

        findViewById(R.id.ivTogglePassword).setOnClickListener(v -> togglePassword());
        findViewById(R.id.ivToggleConfirmPassword).setOnClickListener(v -> toggleConfirmPassword());

        findViewById(R.id.btnAvatarCamera).setOnClickListener(v ->
                Toast.makeText(this, "Profile image upload can be added later.", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnCreateAccount).setOnClickListener(v -> registerUser());

        findViewById(R.id.btnGoogle).setOnClickListener(v ->
                Toast.makeText(this, "Google sign-up is UI demo only.", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnEmail).setOnClickListener(v ->
                Toast.makeText(this, "Email sign-up selected.", Toast.LENGTH_SHORT).show());

        findViewById(R.id.tvLogin).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void styleTermsText(TextView tvTermsText) {
        String text = "I agree to the Terms of Service and Privacy Policy";
        SpannableString spannable = new SpannableString(text);

        int termsStart = text.indexOf("Terms of Service");
        int termsEnd = termsStart + "Terms of Service".length();

        int privacyStart = text.indexOf("Privacy Policy");
        int privacyEnd = privacyStart + "Privacy Policy".length();

        if (termsStart >= 0) {
            spannable.setSpan(
                    new ForegroundColorSpan(Color.parseColor("#009B93")),
                    termsStart,
                    termsEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        if (privacyStart >= 0) {
            spannable.setSpan(
                    new ForegroundColorSpan(Color.parseColor("#009B93")),
                    privacyStart,
                    privacyEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        tvTermsText.setText(spannable);
    }

    private void styleLoginText(TextView tvLogin) {
        String text = "Already have an account? Log in";
        SpannableString spannable = new SpannableString(text);

        int start = text.indexOf("Log in");
        int end = text.length();

        if (start >= 0) {
            spannable.setSpan(
                    new ForegroundColorSpan(Color.parseColor("#009B93")),
                    start,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        tvLogin.setText(spannable);
    }

    private void togglePassword() {
        if (passwordVisible) {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordVisible = false;
        } else {
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            passwordVisible = true;
        }
        etPassword.setSelection(etPassword.getText().length());
    }

    private void toggleConfirmPassword() {
        if (confirmPasswordVisible) {
            etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            confirmPasswordVisible = false;
        } else {
            etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            confirmPasswordVisible = true;
        }
        etConfirmPassword.setSelection(etConfirmPassword.getText().length());
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Please confirm password");
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Password does not match");
            return;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please agree to the terms first.", Toast.LENGTH_SHORT).show();
            return;
        }

        MoodMealPrefs.register(this, fullName, email, password);

        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}