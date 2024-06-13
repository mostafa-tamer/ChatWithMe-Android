package com.mostafatamer.chatwithme.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;

/**
 * @author Mostafa Tamer
 */
public class SharedPreferencesHelper {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor sharedPreferencesEditor;

    public SharedPreferencesHelper(Context context, String sharedName) {
        this.sharedPreferences = context.getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        this.sharedPreferencesEditor = sharedPreferences.edit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public Long getLong(String key) {
        long value = sharedPreferences.getLong(key, Long.MIN_VALUE);
        return value == Long.MIN_VALUE ? null : value;
    }

    public Integer getInt(@NotNull String key) {
        int value = sharedPreferences.getInt(key, Integer.MIN_VALUE);
        return value == Integer.MIN_VALUE ? null : value;
    }

    public void setValue(String key, String value) {
        sharedPreferencesEditor.putString(key, value);
        sharedPreferencesEditor.apply();
    }

    public void setValue(String key, long value) {
        sharedPreferencesEditor.putLong(key, value);
        sharedPreferencesEditor.apply();
    }

    public void setValue(String key, boolean value) {
        sharedPreferencesEditor.putBoolean(key, value);
        sharedPreferencesEditor.apply();
    }

    public void setValue(@NotNull String key, int value) {
        sharedPreferencesEditor.putInt(key, value);
        sharedPreferencesEditor.apply();
    }
}
