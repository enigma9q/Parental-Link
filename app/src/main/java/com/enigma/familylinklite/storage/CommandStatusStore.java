package com.enigma.familylinklite.storage;

import android.content.Context;
import android.content.SharedPreferences;
import com.enigma.familylinklite.core.AppConfig;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class CommandStatusStore {
    private CommandStatusStore() {}
    private static final String KEY = "commandLifecycleHistory";
    private static final int MAX = 30;

    public static void record(Context context, String id, String command, String status, String details, String value) {
        if (context == null) return;
        String cleanId = clean(id);
        String cleanCommand = clean(command);
        String cleanStatus = clean(status);
        String cleanDetails = clean(details);
        String cleanValue = clean(value);
        String line = System.currentTimeMillis() + "|" + cleanId + "|" + cleanCommand + "|" + cleanStatus + "|" + cleanDetails + "|" + cleanValue;
        SharedPreferences p = context.getSharedPreferences(AppConfig.PREFS, 0);
        String old = p.getString(KEY, "");
        String[] lines = old.length() == 0 ? new String[0] : old.split("\\n");
        StringBuilder sb = new StringBuilder(line);
        int kept = 0;
        for (String l : lines) {
            if (l == null || l.trim().length() == 0) continue;
            sb.append("\n").append(l);
            if (++kept >= MAX - 1) break;
        }
        p.edit().putString(KEY, sb.toString()).putString("lastCommandStatus", cleanCommand + ": " + cleanStatus).apply();
    }

    public static String pretty(Context context) {
        if (context == null) return "No command history";
        String raw = context.getSharedPreferences(AppConfig.PREFS, 0).getString(KEY, "");
        if (raw.trim().length() == 0) return "No command history";
        StringBuilder sb = new StringBuilder();
        String[] rows = raw.split("\\n");
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss", Locale.US);
        for (String row : rows) {
            String[] p = row.split("\\|", -1);
            if (p.length < 4) continue;
            long t = 0;
            try { t = Long.parseLong(p[0]); } catch (Exception ignored) {}
            String time = t > 0 ? fmt.format(new Date(t)) : "--:--";
            String icon = iconFor(p[3]);
            sb.append(time).append("  ").append(icon).append(" ").append(p[2]).append(" — ").append(p[3]);
            if (p.length > 4 && p[4].length() > 0) sb.append("\n   ").append(shorten(p[4], 120));
            sb.append("\n\n");
        }
        return sb.toString().trim();
    }

    public static String[] lastFailed(Context context) {
        String raw = context.getSharedPreferences(AppConfig.PREFS, 0).getString(KEY, "");
        for (String row : raw.split("\\n")) {
            String[] p = row.split("\\|", -1);
            if (p.length >= 6 && "Failed".equalsIgnoreCase(p[3])) return new String[]{p[2], p[5]};
        }
        return null;
    }

    public static void clear(Context context) {
        context.getSharedPreferences(AppConfig.PREFS, 0).edit().remove(KEY).remove("lastCommandStatus").apply();
    }

    private static String clean(String s) {
        if (s == null) return "";
        return s.replace("|", "/").replace("\n", " ").trim();
    }

    private static String iconFor(String status) {
        if (status == null) return "•";
        String s = status.toLowerCase(Locale.US);
        if (s.contains("sending")) return "…";
        if (s.contains("delivered")) return "✓";
        if (s.contains("executed") || s.contains("completed")) return "✓✓";
        if (s.contains("failed")) return "❌";
        return "•";
    }

    private static String shorten(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}
