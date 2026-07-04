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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

    private EditText etEmail;
    private EditText etPassword;
    private ImageView ivTogglePassword;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);

        TextView btnLogin = findViewById(R.id.btnLogin);
        View btnGoogle = findViewById(R.id.btnGoogle);
        View btnEmailLink = findViewById(R.id.btnEmailLink);
        TextView tvForgot = findViewById(R.id.tvForgot);
        TextView tvRegister = findViewById(R.id.tvRegister);

        colorRegisterText(tvRegister);

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        tvForgot.setOnClickListener(v -> {
            Toast.makeText(this, "Demo reset: use password 123456 or register new account.", Toast.LENGTH_LONG).show();
        });

        btnGoogle.setOnClickListener(v -> {
            Toast.makeText(this, "Google login is UI demo only. Firebase will be added later.", Toast.LENGTH_LONG).show();
        });

        btnEmailLink.setOnClickListener(v -> {
            Toast.makeText(this, "Email link login is UI demo only. Firebase will be added later.", Toast.LENGTH_LONG).show();
        });

        ivTogglePassword.setOnClickListener(v -> togglePasswordVisibility());
    }

    private void colorRegisterText(TextView tvRegister) {
        String text = "Don’t have an account? Register";
        SpannableString spannable = new SpannableString(text);

        int start = text.indexOf("Register");
        int end = text.length();

        if (start >= 0) {
            spannable.setSpan(
                    new ForegroundColorSpan(Color.parseColor("#009B93")),
                    start,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        tvRegister.setText(spannable);
    }

    private void togglePasswordVisibility() {
        if (passwordVisible) {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordVisible = false;
        } else {
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            passwordVisible = true;
        }

        etPassword.setSelection(etPassword.getText().length());
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            return;
        }

        boolean success = MoodMealPrefs.login(this, email, password);

        if (success) {
            Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Invalid account. Try demo: aiman.hakim@gmail.com / 123456", Toast.LENGTH_LONG).show();
        }
    }
}
