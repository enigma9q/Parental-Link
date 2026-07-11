package com.enigma.familylinklite;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.enigma.familylinklite.services.ChildServerService;

public class ChildServerWatchdogReceiver extends BroadcastReceiver {
    public static final String ACTION_RESTART_CHILD_SERVER = "com.enigma.familylinklite.RESTART_CHILD_SERVER";

    @Override public void onReceive(Context context, Intent intent) {
        if (!shouldRunChildServer(context)) return;
        startChildServer(context, intent == null ? "watchdog" : String.valueOf(intent.getAction()));
    }

    public static boolean shouldRunChildServer(Context context) {
        android.content.SharedPreferences p = context.getSharedPreferences("p", 0);
        String key = p.getString("key", "");
        if (key == null || key.length() == 0) return false;
        if (p.getBoolean("childServerEnabled", false)) return true;
        if (p.getBoolean("childConnected", false) || p.getBoolean("childLinked", false)) return true;
        return "child".equals(p.getString("role", ""));
    }

    public static void startChildServer(Context context, String reason) {
        try {
            Intent service = new Intent(context, ChildServerService.class);
            service.putExtra("restart_reason", reason == null ? "watchdog" : reason);
            if (Build.VERSION.SDK_INT >= 26) context.startForegroundService(service);
            else context.startService(service);
        } catch (Exception ignored) {}
    }

    public static void schedule(Context context, String reason) {
        if (!shouldRunChildServer(context)) return;
        try {
            Intent i = new Intent(context, ChildServerWatchdogReceiver.class).setAction(ACTION_RESTART_CHILD_SERVER);
            i.putExtra("restart_reason", reason == null ? "watchdog" : reason);
            int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
            PendingIntent pi = PendingIntent.getBroadcast(context, 44, i, flags);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (am == null) return;
            long at = System.currentTimeMillis() + 60_000L;
            if (Build.VERSION.SDK_INT >= 23) am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi);
            else am.set(AlarmManager.RTC_WAKEUP, at, pi);
        } catch (Exception ignored) {}
    }
}
