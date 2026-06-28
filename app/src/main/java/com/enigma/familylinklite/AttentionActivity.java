package com.enigma.familylinklite;

import android.app.*;import android.graphics.Color;import android.os.*;import android.view.*;import android.widget.*;

public class AttentionActivity extends Activity{
    boolean blocking=false;
    public void onCreate(Bundle b){super.onCreate(b);getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);String title=getIntent().getStringExtra("title");String text=getIntent().getStringExtra("text");blocking=getIntent().getBooleanExtra("blocking",false);if(title==null)title="Parental-Link";if(text==null)text="Parent action requires attention.";LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setGravity(Gravity.CENTER);root.setPadding(32,32,32,32);root.setBackgroundColor(blocking?Color.rgb(255,245,245):Color.rgb(245,249,252));TextView t=new TextView(this);t.setText(title);t.setTextSize(26);t.setGravity(Gravity.CENTER);root.addView(t);TextView msg=new TextView(this);msg.setText(text);msg.setTextSize(18);msg.setGravity(Gravity.CENTER);root.addView(msg);Button ok=new Button(this);ok.setAllCaps(false);ok.setText(blocking?"Parent unlock required":"OK");root.addView(ok);setContentView(root);ok.setOnClickListener(v->{if(!blocking)finish();});}
    public void onBackPressed(){if(!blocking)super.onBackPressed();}
}
