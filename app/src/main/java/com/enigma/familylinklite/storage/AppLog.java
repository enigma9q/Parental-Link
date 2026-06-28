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
        if (context == null || msg == null) return;

        String safe = msg.replaceAll("(?i)(password|pin|code|secret)[:= ]+[^\\n ]+", "$1: [hidden]");
        String stamp = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());
        String entry = stamp + " " + safe;

        SharedPreferences prefs = context.getSharedPreferences(AppConfig.PREFS, 0);
        String old = prefs.getString("logs", "");
        String combined = entry + (old.length() > 0 ? "\n" + old : "");
        String[] lines = combined.split("\n");

        StringBuilder trimmed = new StringBuilder();
        for (int i = 0; i < Math.min(200, lines.length); i++) {
            if (i > 0) trimmed.append("\n");
            trimmed.append(lines[i]);
        }

        SharedPreferences.Editor editor = prefs.edit()
                .putString("logs", trimmed.toString())
                .putString("latestLog", entry);

        String low = safe.toLowerCase(Locale.US);
        if (low.contains("wrong") || low.contains("invalid") || low.contains("bad handshake") || low.contains("security")) {
            editor.putBoolean("securityAttention", true);
        }
        editor.apply();

        try {
            context.startService(new Intent(context, ParentMonitorService.class));
        } catch (Exception ignored) {}
    }
}
