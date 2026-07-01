package com.enigma.familylinklite.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import java.util.Locale;

public final class LanguageManager {
    private LanguageManager() {}

    public static final String FOLLOW_DEVICE = "system";
    public static final String ENGLISH = "en";
    public static final String GREEK = "el";
    public static final String FRENCH = "fr";

    public static String current(Context c) {
        return c.getSharedPreferences("p",0).getString("appLanguage", FOLLOW_DEVICE);
    }

    public static void set(Context c, String lang) {
        if(!isSupported(lang)) lang = FOLLOW_DEVICE;
        c.getSharedPreferences("p",0).edit().putString("appLanguage", lang).apply();
    }

    public static boolean isSupported(String lang) {
        return FOLLOW_DEVICE.equals(lang) || ENGLISH.equals(lang) || GREEK.equals(lang) || FRENCH.equals(lang);
    }

    public static String label(String lang) {
        if(GREEK.equals(lang)) return "Ελληνικά 🇬🇷";
        if(FRENCH.equals(lang)) return "Français 🇫🇷";
        if(ENGLISH.equals(lang)) return "English 🇬🇧";
        return "Follow device";
    }

    public static String languageMenuLabel(Context c) {
        return "Language: " + label(current(c));
    }

    public static void applySavedLocale(Activity activity) {
        String lang = current(activity);
        if(FOLLOW_DEVICE.equals(lang)) return;
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration(activity.getResources().getConfiguration());
        config.setLocale(locale);
        activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics());
    }

    public static String codeForCommand(String lang) {
        if(!isSupported(lang)) return FOLLOW_DEVICE;
        return lang;
    }
}
