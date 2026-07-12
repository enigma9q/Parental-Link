package com.enigma.familylinklite;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.enigma.familylinklite.chat.ChatIcons;
import com.enigma.familylinklite.storage.ChatStore;

public class ChildQuickActionReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        String action = intent != null ? intent.getAction() : "";
        String iconKey = intent != null ? intent.getStringExtra("emoji") : "chat";
        String text = intent != null ? intent.getStringExtra("text") : "Message from parent";
        if (iconKey == null || iconKey.length() == 0) iconKey = "chat";
        if (text == null || text.length() == 0) text = "Message from parent";

        String status = "no";
        if ("com.enigma.familylinklite.QUICK_SNOOZE".equals(action)) status = "in 5";
        if ("com.enigma.familylinklite.QUICK_DISMISS".equals(action)) status = "yes";

        String stamp = ChatStore.now();
        String label = ChatIcons.titlePrefix(iconKey);
        SharedPreferences.Editor ed = context.getSharedPreferences("p", 0).edit();
        ed.putString("lastParentQuickMessage", label + ": " + text + " at " + stamp);
        ed.putString("lastChildQuickAction", label + ": " + text + " - " + status + " at " + stamp);
        ed.putString("lastParentQuickMessageSeen", status + " at " + stamp).apply();
        ChatStore.append(context, "Child", status, "quick_action");
        try {
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(301);
        } catch (Exception ignored) {}
    }
}
