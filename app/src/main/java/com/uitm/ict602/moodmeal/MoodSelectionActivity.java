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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MoodSelectionActivity extends Activity
        implements SensorEventListener {

    private SpinWheelView spinWheelView;

    private final Random random = new Random();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

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

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ReminderHelper.requestNotificationPermissionIfNeeded(this);

        spinWheelView = findViewById(R.id.spinWheelView);

        setupMoodButtons();
        setupBottomNav();
        setupShakeSensor();
    }

    private void setupMoodButtons() {
        findViewById(R.id.tvBackMood)
                .setOnClickListener(v -> finish());

        findViewById(R.id.moodHungry)
                .setOnClickListener(v ->
                        showRecommendation("Hungry", "button")
                );

        findViewById(R.id.moodStressed)
                .setOnClickListener(v ->
                        showRecommendation("Stressed", "button")
                );

        findViewById(R.id.moodTired)
                .setOnClickListener(v ->
                        showRecommendation("Tired", "button")
                );

        findViewById(R.id.moodHappy)
                .setOnClickListener(v ->
                        showRecommendation("Happy", "button")
                );

        findViewById(R.id.moodSad)
                .setOnClickListener(v ->
                        showRecommendation("Sad", "button")
                );

        findViewById(R.id.moodCraving)
                .setOnClickListener(v ->
                        showRecommendation("Craving", "button")
                );

        findViewById(R.id.moodBudget)
                .setOnClickListener(v ->
                        showRecommendation("Budget Mode", "button")
                );

        findViewById(R.id.moodAdventurous)
                .setOnClickListener(v ->
                        showRecommendation("Adventurous", "button")
                );

        findViewById(R.id.btnSpinMood)
                .setOnClickListener(v -> spinMood("wheel"));
    }

    private void setupShakeSensor() {
        sensorManager =
                (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(
                    Sensor.TYPE_ACCELEROMETER
            );
        }

        if (accelerometer == null) {
            Toast.makeText(
                    MoodSelectionActivity.this,
                    "Accelerometer is not available on this device.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void spinMood(String selectionSource) {
        int index = random.nextInt(moods.length);
        String selectedMood = moods[index];

        float rotation = 1440 + random.nextInt(720);

        spinWheelView.animate()
                .rotationBy(rotation)
                .setDuration(1400)
                .withEndAction(() -> {
                    vibrate();

                    showRecommendation(
                            selectedMood,
                            selectionSource
                    );
                })
                .start();
    }

    private void showRecommendation(
            String mood,
            String selectionSource
    ) {
        String message = getRecommendationMessage(mood);

        saveMoodSelection(
                mood,
                message,
                selectionSource
        );

        ReminderHelper.scheduleFoodReminder(
                this,
                mood,
                15
        );

        Toast.makeText(
                MoodSelectionActivity.this,
                "Food reminder scheduled.",
                Toast.LENGTH_SHORT
        ).show();

        new AlertDialog.Builder(this)
                .setTitle("Mood Selected: " + mood)
                .setMessage(message)
                .setPositiveButton(
                        "View Recommendation",
                        (dialog, which) -> {
                            Intent intent = new Intent(
                                    MoodSelectionActivity.this,
                                    RecommendationActivity.class
                            );

                            intent.putExtra(
                                    "selectedMood",
                                    mood
                            );

                            intent.putExtra(
                                    "recommendationMessage",
                                    message
                            );

                            startActivity(intent);
                        }
                )
                .setNegativeButton(
                        "Back Home",
                        (dialog, which) -> {
                            Intent intent = new Intent(
                                    MoodSelectionActivity.this,
                                    HomeActivity.class
                            );

                            intent.setFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            );

                            startActivity(intent);
                        }
                )
                .show();
    }

    private void saveMoodSelection(
            String mood,
            String recommendationMessage,
            String selectionSource
    ) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(
                    MoodSelectionActivity.this,
                    "Please log in to save your mood.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        DocumentReference userReference =
                db.collection("users")
                        .document(currentUser.getUid());

        DocumentReference historyReference =
                userReference
                        .collection("moodHistory")
                        .document();

        Map<String, Object> latestMoodData =
                new HashMap<>();

        latestMoodData.put("lastMood", mood);
        latestMoodData.put(
                "lastRecommendation",
                recommendationMessage
        );
        latestMoodData.put(
                "lastMoodSource",
                selectionSource
        );
        latestMoodData.put(
                "lastMoodAt",
                FieldValue.serverTimestamp()
        );

        Map<String, Object> historyData =
                new HashMap<>();

        historyData.put("userId", currentUser.getUid());
        historyData.put("mood", mood);
        historyData.put(
                "recommendationMessage",
                recommendationMessage
        );
        historyData.put(
                "selectionSource",
                selectionSource
        );
        historyData.put(
                "selectedAt",
                FieldValue.serverTimestamp()
        );

        WriteBatch batch = db.batch();

        batch.set(
                userReference,
                latestMoodData,
                SetOptions.merge()
        );

        batch.set(
                historyReference,
                historyData
        );

        batch.commit()
                .addOnFailureListener(exception ->
                        Toast.makeText(
                                MoodSelectionActivity.this,
                                "Mood selected, but it could not be saved.",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private String getRecommendationMessage(String mood) {
        switch (mood) {
            case "Hungry":
                return "Recommended food:\n"
                        + "🍛 Rice meals\n"
                        + "🍗 Heavy meals\n"
                        + "🍱 Affordable student set\n\n"
                        + "Suggested place:\n"
                        + "Nasi Ayam Campus";

            case "Stressed":
                return "Recommended food:\n"
                        + "☕ Coffee\n"
                        + "🍰 Dessert\n"
                        + "🍜 Comfort food\n\n"
                        + "Suggested place:\n"
                        + "Kopi Uni";

            case "Tired":
                return "Recommended food:\n"
                        + "☕ Coffee\n"
                        + "🥪 Quick meals\n"
                        + "🍌 Light snacks\n\n"
                        + "Suggested place:\n"
                        + "Campus Cafe Express";

            case "Happy":
                return "Recommended food:\n"
                        + "🍰 Dessert\n"
                        + "🧋 Boba\n"
                        + "🍕 Social food\n\n"
                        + "Suggested place:\n"
                        + "Pizza Lab";

            case "Sad":
                return "Recommended food:\n"
                        + "🍲 Warm soup\n"
                        + "🍫 Sweet food\n"
                        + "🍜 Comfort meals\n\n"
                        + "Suggested place:\n"
                        + "Comfort Bowl Cafe";

            case "Craving":
                return "Recommended food:\n"
                        + "🍟 Fries\n"
                        + "🌶 Spicy food\n"
                        + "🧋 Boba\n\n"
                        + "Suggested place:\n"
                        + "Snack Corner";

            case "Budget Mode":
                return "Recommended food:\n"
                        + "💰 Economy rice\n"
                        + "🍱 Student meal\n"
                        + "🍜 Food court meals\n\n"
                        + "Suggested place:\n"
                        + "Campus Food Court";

            case "Adventurous":
                return "Recommended food:\n"
                        + "🧭 Random menu\n"
                        + "🍔 New restaurant\n"
                        + "🌮 Unique food\n\n"
                        + "Suggested place:\n"
                        + "Hidden Bite Spot";

            default:
                return "Try something delicious nearby!";
        }
    }

    private void setupBottomNav() {
        findViewById(R.id.navHome)
                .setOnClickListener(v -> {
                    startActivity(
                            new Intent(
                                    MoodSelectionActivity.this,
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
                        openActivity(ManageAccountActivity.class)
                );
    }

    private void openActivity(Class<?> activityClass) {
        startActivity(
                new Intent(
                        MoodSelectionActivity.this,
                        activityClass
                )
        );
    }

    @SuppressWarnings("deprecation")
    private void vibrate() {
        Vibrator vibrator =
                (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (vibrator == null) {
            return;
        }

        if (android.os.Build.VERSION.SDK_INT
                >= android.os.Build.VERSION_CODES.O) {

            vibrator.vibrate(
                    VibrationEffect.createOneShot(
                            130,
                            VibrationEffect.DEFAULT_AMPLITUDE
                    )
            );
        } else {
            vibrator.vibrate(130);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sensorManager != null
                && accelerometer != null) {

            sensorManager.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_UI
            );
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
        if (event == null
                || event.sensor.getType()
                != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        float x =
                event.values[0]
                        / SensorManager.GRAVITY_EARTH;

        float y =
                event.values[1]
                        / SensorManager.GRAVITY_EARTH;

        float z =
                event.values[2]
                        / SensorManager.GRAVITY_EARTH;

        double gForce =
                Math.sqrt(
                        x * x
                                + y * y
                                + z * z
                );

        if (gForce > 2.7) {
            long currentTime =
                    System.currentTimeMillis();

            if (currentTime - lastShakeTime > 1300) {
                lastShakeTime = currentTime;

                Toast.makeText(
                        MoodSelectionActivity.this,
                        "Shake detected!",
                        Toast.LENGTH_SHORT
                ).show();

                spinMood("shake");
            }
        }
    }

    @Override
    public void onAccuracyChanged(
            Sensor sensor,
            int accuracy
    ) {
        // No action is required.
    }
}