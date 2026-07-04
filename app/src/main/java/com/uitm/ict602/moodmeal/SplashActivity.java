package com.uitm.ict602.moodmeal;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;

public class SplashActivity extends Activity {

    private static final int SPLASH_DELAY = 1800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ProgressBar splashProgress = findViewById(R.id.splashProgress);

        ObjectAnimator animator = ObjectAnimator.ofInt(splashProgress, "progress", 0, 100);
        animator.setDuration(SPLASH_DELAY);
        animator.start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;

            if (MoodMealPrefs.isLoggedIn(SplashActivity.this)) {
                intent = new Intent(SplashActivity.this, HomeActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }

            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}