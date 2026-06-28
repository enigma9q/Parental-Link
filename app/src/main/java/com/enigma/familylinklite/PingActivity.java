package com.enigma.familylinklite;

import android.app.*;import android.content.*;import android.graphics.Color;import android.os.*;import android.view.*;import android.widget.*;import com.enigma.familylinklite.services.ChildServerService;

public class PingActivity extends Activity{
    public void onCreate(Bundle b){super.onCreate(b);getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setGravity(Gravity.CENTER);root.setPadding(32,32,32,32);root.setBackgroundColor(Color.rgb(255,245,245));TextView title=new TextView(this);title.setText("Your parent is pinging this tablet");title.setTextSize(24);title.setGravity(Gravity.CENTER);root.addView(title);TextView text=new TextView(this);text.setText("Press Stop ping to silence the alert.");text.setTextSize(16);text.setGravity(Gravity.CENTER);root.addView(text);Button stop=new Button(this);stop.setText("Stop ping");stop.setAllCaps(false);root.addView(stop);setContentView(root);stop.setOnClickListener(v->{Intent i=new Intent(this,ChildServerService.class);i.setAction("STOP_PING");startService(i);finish();});}
}
