package com.enigma.familylinklite;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.enigma.familylinklite.storage.ChatStore;

public class ChildQuickActionReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        String action = intent != null ? intent.getAction() : "";
        String emoji = intent != null ? intent.getStringExtra("emoji") : "💬";
        String text = intent != null ? intent.getStringExtra("text") : "Message from parent";
        if (emoji == null || emoji.length() == 0) emoji = "💬";
        if (text == null || text.length() == 0) text = "Message from parent";
        String status = "dismissed";
        if ("com.enigma.familylinklite.QUICK_SNOOZE".equals(action)) status = "snoozed 5 min";
        String stamp = ChatStore.now();
        SharedPreferences.Editor ed = context.getSharedPreferences("p",0).edit();
        ed.putString("lastParentQuickMessage", emoji + " " + text + " at " + stamp);
        ed.putString("lastChildQuickAction", emoji + " " + text + " — " + status + " at " + stamp);
        ed.putString("lastParentQuickMessageSeen", status + " at " + stamp).apply();
        ChatStore.append(context,"Child",status,"quick_action");
        try { ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(301); } catch(Exception ignored) {}
    }
}
