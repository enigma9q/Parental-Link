package com.enigma.familylinklite.calls;

import android.app.*;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.*;
import android.graphics.Color;
import android.app.PendingIntent;
import android.os.Build;
import com.enigma.familylinklite.ui.UiFactory;
import com.enigma.familylinklite.R;

public class CallActivity extends Activity {
    public static final String ACTION_END_CALL = "com.enigma.familylinklite.END_CALL";
    static final int NOTIF_ID = 2701;
    TextView text;
    Button accept;
    long started = 0;
    String peer = "Device";
    android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
    BroadcastReceiver endReceiver = new BroadcastReceiver(){ public void onReceive(Context c, Intent i){ endCall("Call ended"); } };
    Runnable tick = new Runnable(){ public void run(){ if(started>0){ long s=(System.currentTimeMillis()-started)/1000; text.setText("Call active  •  "+(s/60)+":"+String.format(java.util.Locale.US,"%02d",s%60)); showCallNotification(true); handler.postDelayed(this,1000);} } };

    public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if(Build.VERSION.SDK_INT>=33) registerReceiver(endReceiver,new IntentFilter(ACTION_END_CALL),Context.RECEIVER_NOT_EXPORTED);
        else registerReceiver(endReceiver,new IntentFilter(ACTION_END_CALL));
        String mode = getIntent() != null ? getIntent().getStringExtra("mode") : "incoming";
        peer = getIntent() != null ? getIntent().getStringExtra("parent") : "Parent";
        if(peer==null||peer.length()==0)peer="Parent";
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(36, 36, 36, 36);
        root.setBackgroundColor(Color.rgb(218,237,248));
        TextView title = UiFactory.text(this, "Parental-Link call", 26);
        title.setGravity(Gravity.CENTER);
        root.addView(title);
        TextView preview = UiFactory.text(this, "🔗", 72);
        preview.setGravity(Gravity.CENTER);
        root.addView(preview);
        text = UiFactory.text(this, "incoming".equals(mode) ? peer + " is calling this device." : "Calling " + peer + "...", 18);
        text.setGravity(Gravity.CENTER);
        root.addView(text);
        TextView placeholder = UiFactory.text(this, "Audio-only call placeholder. Back minimizes; only End Call closes it.", 14);
        placeholder.setGravity(Gravity.CENTER);
        root.addView(placeholder);
        accept = UiFactory.button(this, "Accept");
        Button decline = UiFactory.button(this, "End call");
        root.addView(accept);
        root.addView(decline);
        accept.setOnClickListener(v -> startCall());
        decline.setOnClickListener(v -> endCall("Call ended"));
        setContentView(root);
        showCallNotification(false);
        if("outgoing".equals(mode)) startCall();
    }

    void startCall(){
        if(started==0){
            started=System.currentTimeMillis();
            getSharedPreferences("p",0).edit().putString("lastCallStatus","Call active").apply();
            accept.setEnabled(false);
            accept.setText("Call active");
            handler.post(tick);
            showCallNotification(true);
        }
    }

    void endCall(String status){
        getSharedPreferences("p",0).edit().putString("lastCallStatus",status).apply();
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel(NOTIF_ID);
        finish();
    }

    void showCallNotification(boolean active){
        NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=26){
            NotificationChannel ch=new NotificationChannel("call","Parental-Link calls",NotificationManager.IMPORTANCE_LOW);
            nm.createNotificationChannel(ch);
        }
        Intent open=new Intent(this,CallActivity.class);
        open.putExtra("mode","active");open.putExtra("parent",peer);
        PendingIntent openPi=PendingIntent.getActivity(this,200,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Intent end=new Intent(this,CallActionReceiver.class);
        end.setAction(ACTION_END_CALL);
        PendingIntent endPi=PendingIntent.getBroadcast(this,201,end,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(this,"call"):new Notification.Builder(this);
        b.setSmallIcon(R.drawable.ic_notification_link)
         .setContentTitle(active?"☎ Call with "+peer:"☎ Calling "+peer)
         .setContentText(active?"Tap to return to call":"Call screen open")
         .setContentIntent(openPi)
         .setOngoing(true)
         .addAction(R.drawable.ic_notification_link,"End call",endPi);
        nm.notify(NOTIF_ID,b.build());
    }

    @Override public void onBackPressed(){ moveTaskToBack(true); }
    @Override protected void onDestroy(){
        handler.removeCallbacksAndMessages(null);
        try{unregisterReceiver(endReceiver);}catch(Exception ignored){}
        super.onDestroy();
    }
}
