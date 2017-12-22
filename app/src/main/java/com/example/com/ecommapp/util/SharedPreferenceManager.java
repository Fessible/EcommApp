package com.example.com.ecommapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.com.ecommapp.application.EcomAplication;

/**
 * 封装sharedpreference
 * Created by rhm on 2017/12/21.
 */

public class SharedPreferenceManager {
    /**
     * Preference文件名
     */
    private static final String SHARE_PREFREENCE_NAME = "ecomm.pre";
    private SharedPreferences preferences;
    private static SharedPreferenceManager mInstance;
    private SharedPreferences.Editor editor;

    private SharedPreferenceManager() {
        preferences = EcomAplication.getInstance().getSharedPreferences(SHARE_PREFREENCE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static SharedPreferenceManager getInstance() {
        if (mInstance == null) {
            synchronized (SharedPreferenceManager.class) {
                if (mInstance == null) {
                    mInstance = new SharedPreferenceManager();
                }
            }
        }
        return mInstance;
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void putLong(String key, Long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public void putFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public int getInt(String key, int def) {
        return preferences.getInt(key, def);
    }

    public float getFloat(String key, float def) {
        return preferences.getFloat(key, def);
    }

    public boolean getBoolean(String key, boolean def) {
        return preferences.getBoolean(key, def);
    }

    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    public boolean isKeyExist(String key) {
        return preferences.contains(key);
    }

}
