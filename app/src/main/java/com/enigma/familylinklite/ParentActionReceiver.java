package com.enigma.familylinklite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.enigma.familylinklite.network.CommandClient;
import com.enigma.familylinklite.storage.SavedConnection;
import java.util.Base64;

public class ParentActionReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();
        if ("com.enigma.familylinklite.ALLOW_CHILD_UNLOCK".equals(action)) {
            send(context, "enable_use", "parent_notification", "Unlock sent");
        } else if ("com.enigma.familylinklite.SNOOZE_CHILD_REQUEST".equals(action)) {
            send(context, "snooze_ask_parent", "15", "Snooze sent");
        }
    }

    void send(Context context, String type, String value, String toast) {
        try {
            SavedConnection c = new SavedConnection(context);
            byte[] key = c.parentKeyBytesOrNull();
            String ip = c.childIp();
            new CommandClient(context).send(ip, key, type, value,
                    ok -> {
                        context.getSharedPreferences("p",0).edit()
                                .putBoolean("childUnlockRequestPending", false)
                                .putString("lastCommandStatus", type + ": Executed")
                                .apply();
                        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                    },
                    err -> {
                        context.getSharedPreferences("p",0).edit()
                                .putString("lastCommandStatus", type + ": Failed")
                                .apply();
                        Toast.makeText(context, "Command failed", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Toast.makeText(context, "Command failed", Toast.LENGTH_SHORT).show();
        }
    }
}
