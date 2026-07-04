package com.uitm.ict602.moodmeal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String mood = intent.getStringExtra("mood");

        if (mood == null || mood.trim().isEmpty()) {
            mood = "your mood";
        }

        ReminderHelper.showFoodReminderNotification(context, mood);
    }
}