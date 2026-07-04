package com.uitm.ict602.moodmeal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

    private EditText etEmail;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        TextView btnLogin = findViewById(R.id.btnLogin);
        TextView btnGoogle = findViewById(R.id.btnGoogle);
        TextView btnEmailLink = findViewById(R.id.btnEmailLink);
        TextView tvForgot = findViewById(R.id.tvForgot);
        TextView tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        tvForgot.setOnClickListener(v -> {
            Toast.makeText(this, "Demo reset: use password 123456 or register new account.", Toast.LENGTH_LONG).show();
        });

        btnGoogle.setOnClickListener(v -> {
            Toast.makeText(this, "Google login is UI demo only. Add Firebase later.", Toast.LENGTH_LONG).show();
        });

        btnEmailLink.setOnClickListener(v -> {
            Toast.makeText(this, "Email link login is UI demo only. Add Firebase later.", Toast.LENGTH_LONG).show();
        });
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