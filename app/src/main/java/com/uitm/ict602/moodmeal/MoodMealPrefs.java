package com.uitm.ict602.moodmeal;

import android.content.Context;
import android.content.SharedPreferences;

public class MoodMealPrefs {

    private static final String PREF_NAME = "moodmeal_prefs";

    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LOGGED_IN = "logged_in";

    public static final String DEMO_EMAIL = "aiman.hakim@gmail.com";
    public static final String DEMO_PASSWORD = "123456";
    public static final String DEMO_NAME = "Aiman Hakim";

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Used by RegisterActivity new version
    public static void register(Context context, String fullName, String email, String password) {
        saveUser(context, fullName, email, password);
    }

    // Used by old RegisterActivity version
    public static void saveUser(Context context, String name, String email, String password) {
        prefs(context).edit()
                .putString(KEY_NAME, name)
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASSWORD, password)
                .putBoolean(KEY_LOGGED_IN, true)
                .apply();
    }

    public static boolean login(Context context, String email, String password) {
        String savedEmail = prefs(context).getString(KEY_EMAIL, "");
        String savedPassword = prefs(context).getString(KEY_PASSWORD, "");

        boolean demoLogin = DEMO_EMAIL.equalsIgnoreCase(email.trim()) && DEMO_PASSWORD.equals(password);
        boolean registeredLogin = savedEmail.equalsIgnoreCase(email.trim()) && savedPassword.equals(password);

        if (demoLogin || registeredLogin) {
            prefs(context).edit().putBoolean(KEY_LOGGED_IN, true).apply();
            return true;
        }

        return false;
    }

    public static boolean isLoggedIn(Context context) {
        return prefs(context).getBoolean(KEY_LOGGED_IN, false);
    }

    public static void logout(Context context) {
        prefs(context).edit()
                .putBoolean(KEY_LOGGED_IN, false)
                .apply();
    }

    public static void deleteAccount(Context context) {
        prefs(context).edit()
                .remove(KEY_NAME)
                .remove(KEY_EMAIL)
                .remove(KEY_PASSWORD)
                .putBoolean(KEY_LOGGED_IN, false)
                .apply();
    }

    public static String getName(Context context) {
        String name = prefs(context).getString(KEY_NAME, "");
        return name.trim().isEmpty() ? DEMO_NAME : name;
    }

    public static String getFullName(Context context) {
        return getName(context);
    }

    public static String getFirstName(Context context) {
        String fullName = getName(context).trim();

        if (fullName.contains(" ")) {
            return fullName.substring(0, fullName.indexOf(" "));
        }

        return fullName;
    }

    public static String getEmail(Context context) {
        String email = prefs(context).getString(KEY_EMAIL, "");
        return email.trim().isEmpty() ? DEMO_EMAIL : email;
    }

    public static void updateProfile(Context context, String name, String email) {
        prefs(context).edit()
                .putString(KEY_NAME, name)
                .putString(KEY_EMAIL, email)
                .apply();
    }

    public static void updatePassword(Context context, String password) {
        prefs(context).edit()
                .putString(KEY_PASSWORD, password)
                .apply();
    }
}