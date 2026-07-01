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
        x = x.replace('\n', ' ').trim();

        String time = "";
        String category = "";
        String summary = x;
        int b1 = x.indexOf("[");
        int b2 = x.indexOf("]");
        if (b1 > 0 && b2 > b1) {
            time = x.substring(0, b1).trim();
            String meta = x.substring(b1 + 1, b2);
            int slash = meta.indexOf('/');
            category = slash >= 0 ? meta.substring(0, slash) : meta;
            summary = x.substring(b2 + 1).trim();
        }

        String icon = iconFor(category, summary);
        if (time.length() > 0) return (time + " " + icon + " " + summary).trim();
        return (icon + " " + summary).trim();
    }

    public static String iconFor(String category, String summary) {
        String c = category == null ? "" : category.toLowerCase(Locale.US);
        String s = summary == null ? "" : summary.toLowerCase(Locale.US);
        if (s.contains("ping")) return "📢";
        if (s.contains("timeout")) return "⏱";
        if (s.contains("disable") || s.contains("lock") || s.contains("bedtime")) return "🔒";
        if (s.contains("unlock") || s.contains("one-time") || s.contains("password")) return "🔐";
        if (s.contains("volume")) return s.contains("mute") ? "🔇" : "🔊";
        if (s.contains("mute")) return "🔇";
        if (s.contains("chat") || s.contains("message")) return "💬";
        if (s.contains("call")) return "☎";
        if (s.contains("app") || s.contains("blocked")) return "📱";
        if (s.contains("usage")) return "📊";
        if (s.contains("connected") || s.contains("reconnect") || s.contains("server") || s.contains("status")) return "🔄";
        if (s.contains("permission") || s.contains("warning") || "security".equals(c)) return "⚠";
        if (s.contains("failed") || s.contains("error")) return "❌";
        if ("commands".equals(c)) return "▶";
        if ("connection".equals(c)) return "🔄";
        if ("security".equals(c)) return "⚠";
        return "•";
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
