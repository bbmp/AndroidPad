package com.robam.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

public class PreferenceUtils {


    static SharedPreferences getPrefs(Context cx) {
        return PreferenceManager.getDefaultSharedPreferences(cx);
    }

    public static boolean containKey(Context cx, String key) {
        return getPrefs(cx).contains(key);
    }

    public static boolean getBool(Context cx, String key, boolean defValue) {
        return getPrefs(cx).getBoolean(key, defValue);
    }

    public static float getFloat(Context cx, String key, float defValue) {
        return getPrefs(cx).getFloat(key, defValue);
    }

    public static int getInt(Context cx, String key, int defValue) {
        return getPrefs(cx).getInt(key, defValue);
    }

    public static long getLong(Context cx, String key, long defValue) {
        return getPrefs(cx).getLong(key, defValue);
    }

    public static String getString(Context cx, String key, String defValue) {
        return getPrefs(cx).getString(key, defValue);
    }

    public static Set<String> getStrings(Context cx, String key, Set<String> defValue) {
        return getPrefs(cx).getStringSet(key, defValue);
    }


    public static void setBool(Context cx, String key, boolean value) {
        SharedPreferences.Editor edit = getPrefs(cx).edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public static void setFloat(Context cx, String key, float value) {
        SharedPreferences.Editor edit = getPrefs(cx).edit();
        edit.putFloat(key, value);
        edit.commit();
    }

    public static void setInt(Context cx, String key, int value) {
        SharedPreferences.Editor edit = getPrefs(cx).edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public static void setLong(Context cx, String key, long value) {
        SharedPreferences.Editor edit = getPrefs(cx).edit();
        edit.putLong(key, value);
        edit.commit();
    }

    public static void setString(Context cx, String key, String value) {
        SharedPreferences.Editor edit = getPrefs(cx).edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static void setStrings(Context cx, String key, Set<String> value) {
        SharedPreferences.Editor edit = getPrefs(cx).edit();
        edit.putStringSet(key, value);
        edit.commit();
    }


    public static void remove(Context cx, String key) {
        SharedPreferences.Editor edit = getPrefs(cx).edit();
        edit.remove(key);
        edit.commit();
    }

    public static void clear(Context cx) {
        SharedPreferences.Editor edit = getPrefs(cx).edit();
        edit.clear();
        edit.commit();
    }


}
