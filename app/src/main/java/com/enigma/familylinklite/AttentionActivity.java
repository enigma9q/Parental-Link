package com.enigma.familylinklite;

import android.app.*;import android.content.*;import android.graphics.Color;import android.os.*;import android.view.*;import android.widget.*;import com.enigma.familylinklite.services.ChildServerService;

public class AttentionActivity extends Activity{
    boolean blocking=false; android.content.BroadcastReceiver closeReceiver;
    public void onCreate(Bundle b){
        super.onCreate(b);
        blocking=getIntent().getBooleanExtra("blocking",false);
        String title=getIntent().getStringExtra("title");String text=getIntent().getStringExtra("text");
        if(title==null)title=getString(R.string.app_name);if(text==null)text=getString(R.string.parent_action_attention);
        keepFrontVisuals();
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setGravity(Gravity.CENTER);root.setPadding(40,40,40,40);root.setBackgroundColor(Color.WHITE);
        TextView overlayIcon=new TextView(this);overlayIcon.setText(iconFor(title));overlayIcon.setTextSize(56);overlayIcon.setGravity(Gravity.CENTER);root.addView(overlayIcon,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView t=new TextView(this);t.setText(title);t.setTextSize(30);t.setTextColor(Color.BLACK);t.setGravity(Gravity.CENTER);t.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);root.addView(t,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView msg=new TextView(this);msg.setText(text);msg.setTextSize(20);msg.setTextColor(Color.BLACK);msg.setGravity(Gravity.CENTER);msg.setPadding(0,24,0,24);root.addView(msg,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        if(blocking){TextView hint=new TextView(this);hint.setText(getString(R.string.blocking_hint));hint.setTextSize(15);hint.setTextColor(Color.DKGRAY);hint.setGravity(Gravity.CENTER);hint.setPadding(0,8,0,18);root.addView(hint,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));Button ask=new Button(this);ask.setAllCaps(false);ask.setText(getString(R.string.ask_parent));ask.setTextSize(18);ask.setPadding(24,16,24,16);root.addView(ask,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));long snoozeUntil=getSharedPreferences("rules",0).getLong("ask_parent_snooze_until",0);long now=System.currentTimeMillis();if(snoozeUntil>now){ask.setEnabled(false);ask.setText("Ask again in "+Math.max(1,(snoozeUntil-now+59999)/60000)+" min");}final String reason=getIntent().getStringExtra("reason")!=null?getIntent().getStringExtra("reason"):title;ask.setOnClickListener(v->{Intent si=new Intent(this,ChildServerService.class);si.setAction("ASK_PARENT_UNLOCK");si.putExtra("reason",reason);startService(si);ask.setEnabled(false);ask.setText(getString(R.string.request_sent));});}
        else{Button ok=new Button(this);ok.setAllCaps(false);ok.setText(getString(R.string.ok));root.addView(ok);ok.setOnClickListener(v->finish());}
        setContentView(root);
        closeReceiver=new android.content.BroadcastReceiver(){public void onReceive(android.content.Context c,android.content.Intent i){finish();}};
        if(android.os.Build.VERSION.SDK_INT>=33)registerReceiver(closeReceiver,new android.content.IntentFilter("com.enigma.familylinklite.CLOSE_ATTENTION"),RECEIVER_NOT_EXPORTED);else registerReceiver(closeReceiver,new android.content.IntentFilter("com.enigma.familylinklite.CLOSE_ATTENTION"));
    }
    String iconFor(String title){String s=title==null?"":title.toLowerCase(java.util.Locale.US);if(s.contains("ping"))return "📢";if(s.contains("timeout"))return "⏱";if(s.contains("disabled"))return "🔒";if(s.contains("blocked"))return "🚫";if(s.contains("call"))return "📞";if(s.contains("message")||s.contains("chat"))return "💬";return "ℹ️";}

    void keepFrontVisuals(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        if(Build.VERSION.SDK_INT>=27)getWindow().getAttributes().layoutInDisplayCutoutMode=WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        if(Build.VERSION.SDK_INT>=21)getWindow().setStatusBarColor(Color.WHITE);
    }
    protected void onResume(){super.onResume();keepFrontVisuals();}
    public void onWindowFocusChanged(boolean hasFocus){super.onWindowFocusChanged(hasFocus);if(hasFocus)keepFrontVisuals();}
    public void onBackPressed(){if(!blocking)super.onBackPressed();}
    protected void onDestroy(){try{if(closeReceiver!=null)unregisterReceiver(closeReceiver);}catch(Exception ignored){}super.onDestroy();}
}
