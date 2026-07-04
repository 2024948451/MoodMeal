package com.uitm.ict602.moodmeal;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

public class ReminderHelper {

    private static final String CHANNEL_ID = "moodmeal_food_reminder";
    private static final String CHANNEL_NAME = "MoodMeal Food Reminder";

    public static void requestNotificationPermissionIfNeeded(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (activity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                activity.requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        500
                );
            }
        }
    }

    public static void scheduleFoodReminder(Context context, String mood, int secondsFromNow) {
        createNotificationChannel(context);

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("mood", mood);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                101,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerTime = System.currentTimeMillis() + (secondsFromNow * 1000L);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) {
            return;
        }

        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
        );
    }

    public static void showFoodReminderNotification(Context context, String mood) {
        createNotificationChannel(context);

        Intent openIntent = new Intent(context, MoodSelectionActivity.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                202,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(context);
        }

        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("MoodMeal Reminder")
                .setContentText("Feeling " + mood + "? Open MoodMeal for a food suggestion.")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(303, builder.build());
        }
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            channel.setDescription("Food recommendation reminder for MoodMeal.");

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}