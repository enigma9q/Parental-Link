package com.enigma.familylinklite.storage;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class ChatStore {
    private ChatStore() {}

    private static final String PREF = "p";
    private static final String KEY = "chatHistory";
    private static final int MAX = 20;

    public static String now() {
        return new SimpleDateFormat("HH:mm", Locale.US).format(new Date());
    }

    public static void append(Context c, String who, String text, String status) {
        if (text == null) text = "";
        if (who == null || who.length() == 0) who = "Chat";
        if (status == null) status = "";
        String safe = now() + "  " + who + ": " + text + (status.length() > 0 ? "  " + status : "");
        SharedPreferences p = c.getSharedPreferences(PREF, 0);
        String old = p.getString(KEY, "");
        String[] lines = old.length() == 0 ? new String[0] : old.split("\\n");
        StringBuilder sb = new StringBuilder();
        int start = Math.max(0, lines.length - (MAX - 1));
        for (int i = start; i < lines.length; i++) {
            if (lines[i].trim().length() > 0) sb.append(lines[i]).append('\n');
        }
        sb.append(safe);
        p.edit().putString(KEY, sb.toString()).apply();
    }

    public static String history(Context c) {
        return c.getSharedPreferences(PREF, 0).getString(KEY, "");
    }

    public static String lastLines(Context c, int maxLines) {
        String h = history(c);
        if (h.length() == 0) return "No chat messages yet.";
        String[] lines = h.split("\\n");
        StringBuilder sb = new StringBuilder();
        int start = Math.max(0, lines.length - Math.max(1, maxLines));
        for (int i = start; i < lines.length; i++) {
            if (lines[i].trim().length() > 0) sb.append(lines[i]).append('\n');
        }
        return sb.toString().trim();
    }
}
