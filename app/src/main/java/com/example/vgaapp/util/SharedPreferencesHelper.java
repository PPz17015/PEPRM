package com.example.vgaapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private SharedPreferences prefs;

    public SharedPreferencesHelper(Context context) {
        prefs = context.getSharedPreferences("VGA_APP_PREFS", Context.MODE_PRIVATE);
    }

    public void saveUserId(long userId) {
        prefs.edit().putLong("USER_ID", userId).apply();
    }

    public long getUserId() {
        return prefs.getLong("USER_ID", -1L);
    }

    public void saveUserRole(String role) {
        prefs.edit().putString("USER_ROLE", role).apply();
    }

    public String getUserRole() {
        return prefs.getString("USER_ROLE", null);
    }

    public void clearUserData() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return getUserId() != -1L;
    }
}
