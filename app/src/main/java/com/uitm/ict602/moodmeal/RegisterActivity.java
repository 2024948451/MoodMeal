package com.uitm.ict602.moodmeal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirm;
    private CheckBox cbTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etNameRegister);
        etEmail = findViewById(R.id.etEmailRegister);
        etPassword = findViewById(R.id.etPasswordRegister);
        etConfirm = findViewById(R.id.etConfirmRegister);
        cbTerms = findViewById(R.id.cbTerms);

        TextView btnCreate = findViewById(R.id.btnCreateAccount);
        TextView tvLogin = findViewById(R.id.tvLoginRegister);
        TextView tvBack = findViewById(R.id.tvBackRegister);
        TextView btnGoogle = findViewById(R.id.btnGoogleRegister);
        TextView btnEmail = findViewById(R.id.btnEmailRegister);

        btnCreate.setOnClickListener(v -> createAccount());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        tvBack.setOnClickListener(v -> finish());

        btnGoogle.setOnClickListener(v -> {
            Toast.makeText(this, "Google register is UI demo only. Add Firebase later.", Toast.LENGTH_LONG).show();
        });

        btnEmail.setOnClickListener(v -> {
            Toast.makeText(this, "Email register is UI demo only. Use Create Account button.", Toast.LENGTH_LONG).show();
        });
    }

    private void createAccount() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirm = etConfirm.getText().toString();

        if (name.isEmpty()) {
            etName.setError("Full name is required");
            return;
        }

        if (email.isEmpty() || !email.contains("@")) {
            etEmail.setError("Valid email is required");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirm)) {
            etConfirm.setError("Password does not match");
            return;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please agree to Terms and Privacy Policy", Toast.LENGTH_SHORT).show();
            return;
        }

        MoodMealPrefs.saveUser(this, name, email, password);

        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}