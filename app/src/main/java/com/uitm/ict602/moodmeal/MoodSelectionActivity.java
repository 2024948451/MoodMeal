package com.uitm.ict602.moodmeal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import java.util.Random;

public class MoodSelectionActivity extends Activity implements SensorEventListener {

    private SpinWheelView spinWheelView;
    private final Random random = new Random();

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastShakeTime = 0;

    private final String[] moods = {
            "Hungry",
            "Stressed",
            "Tired",
            "Happy",
            "Sad",
            "Craving",
            "Budget Mode",
            "Adventurous"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_selection);

        ReminderHelper.requestNotificationPermissionIfNeeded(this);

        spinWheelView = findViewById(R.id.spinWheelView);

        setupMoodButtons();
        setupBottomNav();
        setupShakeSensor();
    }

    private void setupMoodButtons() {
        findViewById(R.id.tvBackMood).setOnClickListener(v -> finish());

        findViewById(R.id.moodHungry).setOnClickListener(v -> showRecommendation("Hungry"));
        findViewById(R.id.moodStressed).setOnClickListener(v -> showRecommendation("Stressed"));
        findViewById(R.id.moodTired).setOnClickListener(v -> showRecommendation("Tired"));
        findViewById(R.id.moodHappy).setOnClickListener(v -> showRecommendation("Happy"));
        findViewById(R.id.moodSad).setOnClickListener(v -> showRecommendation("Sad"));
        findViewById(R.id.moodCraving).setOnClickListener(v -> showRecommendation("Craving"));
        findViewById(R.id.moodBudget).setOnClickListener(v -> showRecommendation("Budget Mode"));
        findViewById(R.id.moodAdventurous).setOnClickListener(v -> showRecommendation("Adventurous"));

        findViewById(R.id.btnSpinMood).setOnClickListener(v -> spinMood());
    }

    private void setupShakeSensor() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        if (accelerometer == null) {
            Toast.makeText(this, "Accelerometer not available on this device.", Toast.LENGTH_SHORT).show();
        }
    }

    private void spinMood() {
        int index = random.nextInt(moods.length);
        String selectedMood = moods[index];

        float rotation = 1440 + random.nextInt(720);

        spinWheelView.animate()
                .rotationBy(rotation)
                .setDuration(1400)
                .withEndAction(() -> {
                    vibrate();
                    showRecommendation(selectedMood);
                })
                .start();
    }

    private void showRecommendation(String mood) {
        String message = getRecommendationMessage(mood);

        ReminderHelper.scheduleFoodReminder(this, mood, 15);

        Toast.makeText(this, "Food reminder scheduled for demo.", Toast.LENGTH_SHORT).show();

        new AlertDialog.Builder(this)
                .setTitle("Mood Selected: " + mood)
                .setMessage(message)
                .setPositiveButton("View Recommendation", (dialog, which) -> {
                    Intent intent = new Intent(this, RecommendationActivity.class);
                    intent.putExtra("selectedMood", mood);
                    intent.putExtra("recommendationMessage", message);
                    startActivity(intent);
                })
                .setNegativeButton("Back Home", (dialog, which) -> {
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                })
                .show();
    }

    private String getRecommendationMessage(String mood) {
        switch (mood) {
            case "Hungry":
                return "Recommended food:\n🍛 Rice meals\n🍗 Heavy meals\n🍱 Affordable student set\n\nSuggested place:\nNasi Ayam Campus";

            case "Stressed":
                return "Recommended food:\n☕ Coffee\n🍰 Dessert\n🍜 Comfort food\n\nSuggested place:\nKopi Uni";

            case "Tired":
                return "Recommended food:\n☕ Coffee\n🥪 Quick meals\n🍌 Light snacks\n\nSuggested place:\nCampus Cafe Express";

            case "Happy":
                return "Recommended food:\n🍰 Dessert\n🧋 Boba\n🍕 Social food\n\nSuggested place:\nPizza Lab";

            case "Sad":
                return "Recommended food:\n🍲 Warm soup\n🍫 Sweet food\n🍜 Comfort meals\n\nSuggested place:\nComfort Bowl Cafe";

            case "Craving":
                return "Recommended food:\n🍟 Fries\n🌶 Spicy food\n🧋 Boba\n\nSuggested place:\nSnack Corner";

            case "Budget Mode":
                return "Recommended food:\n💰 Economy rice\n🍱 Student meal\n🍜 Food court meals\n\nSuggested place:\nCampus Food Court";

            case "Adventurous":
                return "Recommended food:\n🧭 Random menu\n🍔 New restaurant\n🌮 Unique food\n\nSuggested place:\nHidden Bite Spot";

            default:
                return "Try something delicious nearby!";
        }
    }

    private void setupBottomNav() {
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        findViewById(R.id.navExplore).setOnClickListener(v ->
                startActivity(new Intent(this, MapActivity.class)));

        findViewById(R.id.navPlus).setOnClickListener(v ->
                startActivity(new Intent(this, ReviewActivity.class)));

        findViewById(R.id.navFav).setOnClickListener(v ->
                startActivity(new Intent(this, FavouriteActivity.class)));

        findViewById(R.id.navProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ManageAccountActivity.class)));
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (vibrator == null) return;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(130, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(130);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event == null || event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;

        float x = event.values[0] / SensorManager.GRAVITY_EARTH;
        float y = event.values[1] / SensorManager.GRAVITY_EARTH;
        float z = event.values[2] / SensorManager.GRAVITY_EARTH;

        double gForce = Math.sqrt(x * x + y * y + z * z);

        if (gForce > 2.7) {
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastShakeTime > 1300) {
                lastShakeTime = currentTime;
                Toast.makeText(this, "Shake detected!", Toast.LENGTH_SHORT).show();
                spinMood();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed.
    }
}