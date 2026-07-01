package com.enigma.familylinklite;

import android.app.*;import android.content.*;import android.graphics.Color;import android.os.*;import android.view.*;import android.widget.*;import com.enigma.familylinklite.services.ChildServerService;

public class PingActivity extends Activity{
    BroadcastReceiver closeReceiver;
    public void onCreate(Bundle b){super.onCreate(b);getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setGravity(Gravity.CENTER);root.setPadding(32,32,32,32);root.setBackgroundColor(Color.WHITE);TextView title=new TextView(this);title.setText("📢\nYour parent is pinging this tablet");title.setTextSize(24);title.setTextColor(Color.BLACK);title.setGravity(Gravity.CENTER);root.addView(title);TextView text=new TextView(this);text.setText("Press Stop ping to silence the chime.");text.setTextSize(16);text.setTextColor(Color.DKGRAY);text.setGravity(Gravity.CENTER);root.addView(text);Button stop=new Button(this);stop.setText("Stop ping");stop.setAllCaps(false);root.addView(stop);setContentView(root);if(!getSharedPreferences("rules",0).getBoolean("ping_active",true))finish();stop.setOnClickListener(v->{Intent i=new Intent(this,ChildServerService.class);i.setAction("STOP_PING");startService(i);finish();});
        closeReceiver=new BroadcastReceiver(){public void onReceive(Context c,Intent i){finish();}};
        IntentFilter f=new IntentFilter("com.enigma.familylinklite.CLOSE_PING");
        if(Build.VERSION.SDK_INT>=33)registerReceiver(closeReceiver,f,RECEIVER_NOT_EXPORTED);else registerReceiver(closeReceiver,f);
    }
    protected void onResume(){super.onResume();if(!getSharedPreferences("rules",0).getBoolean("ping_active",true))finish();}
    public void onBackPressed(){}
    protected void onDestroy(){try{if(closeReceiver!=null)unregisterReceiver(closeReceiver);}catch(Exception ignored){}super.onDestroy();}
}
