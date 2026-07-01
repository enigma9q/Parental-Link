package com.enigma.familylinklite.ui;

import android.content.SharedPreferences;

import java.util.Locale;

public final class StatusFormatter {
    private StatusFormatter() {}

    public static String conciseDndStatus(SharedPreferences prefs) {
        String raw = prefs.getString("remoteDnd", "").toLowerCase(Locale.US);
        if (raw.length() == 0 || raw.contains("unknown")) return "???";
        if (raw.startsWith("off") || raw.contains("filter_all")) return "OFF";
        return "ON";
    }

    public static String wifiBars(int bars) {
        if (bars <= 0) return "▱▱▱";
        if (bars == 1) return "▰▱▱";
        if (bars == 2) return "▰▰▱";
        return "▰▰▰";
    }

    public static String activeSyncText(SharedPreferences prefs, boolean live) {
        long last = prefs.getLong("lastStatusMs", 0);
        if (live) return "Live connection";
        if (last <= 0) return "Last sync: never • Next check: --";
        long age = Math.max(0, (System.currentTimeMillis() - last) / 60000);
        long next = Math.max(0, 5 - age);
        String ago = age == 0 ? "just now" : age + " min ago";
        return "Last sync: " + ago + " • Next check: " + next + " min";
    }

    public static String dashboardStrip(SharedPreferences prefs, int parentVolume) {
        int battery = prefs.getInt("remoteBattery", -1);
        boolean charging = prefs.getBoolean("remoteCharging", false);
        String batt = (charging ? "🔌" : "🔋") + (battery >= 0 ? battery + "%" : "--");
        String wifi = "🛜" + wifiBars(prefs.getInt("remoteWifiBars", -1));
        String vol = "🔊" + parentVolume + "%";
        String dnd = "🔕" + conciseDndStatus(prefs);
        return batt + "      " + wifi + "      " + vol + "      " + dnd;
    }

    public static String deviceStatusDetails(SharedPreferences prefs, int parentVolume) {
        int battery = prefs.getInt("remoteBattery", -1);
        return "Battery: " + (battery >= 0 ? battery + "%" : "unknown")
                + "\nCharging: " + (prefs.getBoolean("remoteCharging", false) ? "yes" : "no")
                + "\nWi-Fi: " + wifiBars(prefs.getInt("remoteWifiBars", -1))
                + "\nVolume max: " + parentVolume + "%"
                + "\nDND: " + conciseDndStatus(prefs);
    }
}
