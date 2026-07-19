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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;


public class LoginActivity extends Activity {

    private EditText etEmail;
    private EditText etPassword;
    private ImageView ivTogglePassword;
    private TextView btnLogin;

    private FirebaseAuth mAuth;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        initializeTextStyle();
        initializeClickListeners();
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void initializeTextStyle() {
        TextView tvRegister = findViewById(R.id.tvRegister);
        colorRegisterText(tvRegister);
    }

    private void initializeClickListeners() {
        TextView tvForgot = findViewById(R.id.tvForgot);
        TextView tvRegister = findViewById(R.id.tvRegister);

        View btnGoogle = findViewById(R.id.btnGoogle);
        View btnEmailLink = findViewById(R.id.btnEmailLink);

        btnLogin.setOnClickListener(v -> loginUser());

        ivTogglePassword.setOnClickListener(
                v -> togglePasswordVisibility()
        );

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });

        tvForgot.setOnClickListener(v -> resetPassword());

        btnGoogle.setOnClickListener(v ->
                Toast.makeText(
                        LoginActivity.this,
                        "Google login is currently unavailable.",
                        Toast.LENGTH_SHORT
                ).show()
        );

        btnEmailLink.setOnClickListener(v ->
                Toast.makeText(
                        LoginActivity.this,
                        "Email-link login is currently unavailable.",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    private void colorRegisterText(TextView tvRegister) {
        String text = "Don't have an account? Register";
        String highlightedText = "Register";

        SpannableString spannable = new SpannableString(text);

        int start = text.indexOf(highlightedText);
        int end = start + highlightedText.length();

        spannable.setSpan(
                new ForegroundColorSpan(Color.parseColor("#009B93")),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        tvRegister.setText(spannable);
    }

    private void togglePasswordVisibility() {
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

        etPassword.setSelection(etPassword.getText().length());
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (!validateLoginInput(email, password)) {
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText(R.string.logging_in);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText(R.string.login_button);

                    if (task.isSuccessful()) {
                        Toast.makeText(
                                LoginActivity.this,
                                "Welcome back!",
                                Toast.LENGTH_SHORT
                        ).show();

                        openHomeActivity();
                    } else {
                        showLoginError(task.getException());
                    }
                });
    }

    private boolean validateLoginInput(
            String email,
            String password
    ) {
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

        return true;
    }

    private void showLoginError(Exception exception) {
        String message = "Login failed. Please try again.";

        if (exception instanceof FirebaseAuthInvalidUserException) {
            message = "No account was found with this email.";

        } else if (exception
                instanceof FirebaseAuthInvalidCredentialsException) {
            message = "Incorrect email or password.";

        } else if (exception
                instanceof FirebaseTooManyRequestsException) {
            message = "Too many attempts. Please try again later.";

        } else if (exception != null
                && exception.getMessage() != null) {
            message = exception.getMessage();
        }

        Toast.makeText(
                LoginActivity.this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    private void resetPassword() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError(
                    "Enter your email to reset your password"
            );
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(
                                LoginActivity.this,
                                "Password reset email sent.",
                                Toast.LENGTH_LONG
                        ).show();

                    } else {
                        String message =
                                "Unable to send password reset email.";

                        if (task.getException() != null
                                && task.getException().getMessage() != null) {
                            message = task.getException().getMessage();
                        }

                        Toast.makeText(
                                LoginActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void openHomeActivity() {
        Intent intent = new Intent(
                LoginActivity.this,
                HomeActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}