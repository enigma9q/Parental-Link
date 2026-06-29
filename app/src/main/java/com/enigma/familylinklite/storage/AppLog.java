package com.enigma.familylinklite.storage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.enigma.familylinklite.services.ParentMonitorService;
import com.enigma.familylinklite.core.AppConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class AppLog {
    private AppLog() {}

    public static void add(Context context, String msg) {
        add(context, classifyCategory(msg), classifySeverity(msg), oneLine(msg), msg);
    }

    public static void add(Context context, String category, String severity, String summary, String details) {
        if (context == null || summary == null) return;
        String safeSummary = sanitize(summary).replace('\n', ' ').trim();
        String safeDetails = sanitize(details == null ? summary : details).trim();
        String stamp = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());
        String entry = stamp + " [" + category + "/" + severity + "] " + safeSummary + " || " + safeDetails;

        SharedPreferences prefs = context.getSharedPreferences(AppConfig.PREFS, 0);
        String old = prefs.getString("logs", "");
        String combined = entry + (old.length() > 0 ? "\n" + old : "");
        String[] lines = combined.split("\n");

        StringBuilder trimmed = new StringBuilder();
        for (int i = 0; i < Math.min(300, lines.length); i++) {
            if (i > 0) trimmed.append("\n");
            trimmed.append(lines[i]);
        }

        SharedPreferences.Editor editor = prefs.edit()
                .putString("logs", trimmed.toString())
                .putString("latestLog", lineSummary(entry));

        if ("Security".equals(category) || "Error".equals(severity)) editor.putBoolean("securityAttention", true);
        editor.apply();

        try { context.startService(new Intent(context, ParentMonitorService.class)); } catch (Exception ignored) {}
    }

    public static String lineSummary(String raw) {
        if (raw == null) return "";
        String x = raw;
        int idx = x.indexOf(" || ");
        if (idx >= 0) x = x.substring(0, idx);
        return x.replace('\n', ' ').trim();
    }

    public static String detail(String raw) {
        if (raw == null) return "";
        String[] parts = raw.split(" \\|\\| ", 2);
        if (parts.length < 2) return raw;
        return lineSummary(raw) + "\n\n" + parts[1];
    }

    private static String sanitize(String value) {
        if (value == null) return "";
        return value.replaceAll("(?i)(password|pin|code|secret)[:= ]+[^\\n ]+", "$1: [hidden]");
    }

    private static String oneLine(String msg) {
        if (msg == null) return "";
        String first = msg.split("\\n", 2)[0];
        return first.length() > 120 ? first.substring(0, 120) + "…" : first;
    }

    private static String classifyCategory(String msg) {
        String low = msg == null ? "" : msg.toLowerCase(Locale.US);
        if (low.contains("wrong") || low.contains("invalid") || low.contains("password") || low.contains("admin") || low.contains("security") || low.contains("removal") || low.contains("pairing")) return "Security";
        if (low.contains("connected") || low.contains("disconnected") || low.contains("server") || low.contains("heartbeat") || low.contains("status")) return "Connection";
        if (low.contains("executed") || low.contains("failed") || low.contains("command") || low.contains("ping") || low.contains("volume") || low.contains("lock")) return "Commands";
        return "Activity";
    }

    private static String classifySeverity(String msg) {
        String low = msg == null ? "" : msg.toLowerCase(Locale.US);
        if (low.contains("failed") || low.contains("wrong") || low.contains("invalid") || low.contains("disabled") || low.contains("bad handshake")) return "Error";
        if (low.contains("missing") || low.contains("warning") || low.contains("mismatch") || low.contains("stale")) return "Warning";
        return "Info";
    }
}
