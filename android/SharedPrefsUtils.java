package com.portalp.technician.model.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.portalp.com_library.model.utils.Units;

/**
 * Helper class meant to interact with the Android {@link SharedPreferences}.
 */
public class SharedPrefsUtils {

    private static final String SHARED_PREFS_FILE_NAME = "shared_prefs_portalp_installer";

    public static final String KEY_LANGUAGE = "KEY_LANGUAGE";
    public static final String KEY_FORCE_CHMOD = "KEY_FORCE_CHMOD";
    public static final String KEY_UNIT = "KEY_UNIT_";
    public static final String KEY_LOGIN = "login";

    public static SharedPreferences.Editor getSharedPrefEditor(Context context, String pref) {
        return context.getSharedPreferences(pref, Context.MODE_PRIVATE).edit();
    }

    public static void save(SharedPreferences.Editor editor) {
        editor.apply();
    }

    /**
     * Saves int data.
     *
     * @param context the context
     * @param key     the key
     * @param val     the val
     */
    public static void save(Context context, String key, int val) {
        context.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE).edit().putInt(key, val).apply();
    }

    public static void save(Context context, String key, long val) {
        context.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE).edit().putLong(key, val).apply();
    }

    /**
     * Gets int data.
     *
     * @param context the context
     * @param key     the key
     * @return the int
     */
    public static int getInt(Context context, String key) {
        return context.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE).getInt(key, 0);
    }

    /**
     * Saves String data.
     *
     * @param context the context
     * @param key     the key
     * @param val     the val
     */
    public static void save(Context context, String key, String val) {
        context.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE).edit().putString(key, val).apply();
    }

    /**
     * Gets String data.
     *
     * @param context the context
     * @param key     the key
     * @return the String ! could be null
     */
    public static String getString(Context context, String key) {
        return context.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE).getString(key, null);
    }


    /**
     * Saves boolean data.
     *
     * @param context the context
     * @param key     the key
     * @param val     the val
     */
    public static void save(Context context, String key, boolean val) {
        context.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(key, val)
                .apply();
    }

    /**
     * Gets boolean data.
     *
     * @param context the context
     * @param key     the key
     * @return the boolean
     */
    public static boolean getBooleanData(Context context, String key) {
        return context.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE).getBoolean(key, false);
    }

    /**
     * Saves {@link Units.DisplayUnit} data.
     *
     * @param context  the {@link Context}
     * @param doorUnit {@link Units.DoorUnit}
     * @param displayUnit {@link Units.DisplayUnit}
     */
    public static void save(Context context, Units.DoorUnit doorUnit, Units.DisplayUnit displayUnit) {
        context.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_UNIT + doorUnit, displayUnit.ordinal())
                .apply();
    }

    /**
     * Gets the user favorite {@link Units.DisplayUnit} for the given {@link Units.DoorUnit}, or the default value.
     *
     * @param context  the {@link Context}
     * @param doorUnit {@link Units.DoorUnit}
     * @return {@link Units.DisplayUnit}
     */
    public static Units.DisplayUnit getUnit(Context context, Units.DoorUnit doorUnit) {
        int unitOrdinal = context.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE).getInt(KEY_UNIT + doorUnit, -1);
        if (unitOrdinal == -1) {
            return Units.DisplayUnit.getDefaultDisplayUnit(doorUnit);
        } else {
            return Units.DisplayUnit.values()[unitOrdinal];
        }
    }

}
