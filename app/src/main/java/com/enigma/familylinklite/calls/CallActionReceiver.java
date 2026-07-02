package com.enigma.familylinklite.calls;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CallActionReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(CallActivity.ACTION_END_CALL);
        i.setPackage(context.getPackageName());
        context.sendBroadcast(i);
        context.getSharedPreferences("p",0).edit().putString("lastCallStatus","Call ended from notification").apply();
    }
}
