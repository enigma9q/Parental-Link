package com.enigma.familylinklite.chat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.enigma.familylinklite.storage.ChatStore;
import com.enigma.familylinklite.ui.UiFactory;

public class QuickChatOverlayActivity extends Activity {
    String emoji;
    String text;
    String mode;
    boolean blocking;
    int minutes;
    String reason;

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        emoji=extra("emoji","💬");
        text=extra("text","Message from parent");
        mode=extra("mode","full");
        reason=extra("reason","Message from parent");
        blocking=getIntent()!=null&&getIntent().getBooleanExtra("blocking",false);
        minutes=getIntent()!=null?getIntent().getIntExtra("minutes",0):0;
        if("banner".equals(mode)) showBanner(); else showFullScreen();
    }

    String extra(String name,String fallback){String v=getIntent()!=null?getIntent().getStringExtra(name):fallback;return v==null||v.length()==0?fallback:v;}

    void showBanner(){
        Window w=getWindow();
        w.setStatusBarColor(Color.TRANSPARENT);
        WindowManager.LayoutParams lp=w.getAttributes();
        lp.gravity=Gravity.TOP|Gravity.RIGHT;
        lp.width=(int)(getResources().getDisplayMetrics().widthPixels*0.88f);
        lp.height=WindowManager.LayoutParams.WRAP_CONTENT;
        lp.x=UiFactory.dp(this,8);
        lp.y=UiFactory.dp(this,24);
        w.setAttributes(lp);
        buildContent(false);
        int autoMinutes=minutes>0?minutes:5;
        new Handler(Looper.getMainLooper()).postDelayed(()->{recordAction("expired after "+autoMinutes+" min");finish();},autoMinutes*60000L);
    }

    void showFullScreen(){
        getWindow().setStatusBarColor(UiFactory.bg(this));
        buildContent(true);
    }

    void buildContent(boolean full){
        LinearLayout root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(full?Gravity.CENTER:Gravity.CENTER_VERTICAL);
        root.setPadding(UiFactory.dp(this,18),UiFactory.dp(this,16),UiFactory.dp(this,18),UiFactory.dp(this,16));
        root.setBackground(UiFactory.rounded(this,UiFactory.panel(this),full?0:20));

        TextView emojiView=UiFactory.text(this,emoji,full?76:42);
        emojiView.setGravity(Gravity.CENTER);
        root.addView(emojiView,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView body=UiFactory.text(this,text,full?28:18);
        body.setGravity(Gravity.CENTER);
        body.setPadding(0,UiFactory.dp(this,8),0,UiFactory.dp(this,12));
        root.addView(body,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));

        if(blocking){
            TextView reasonView=UiFactory.text(this,reason,18);
            reasonView.setGravity(Gravity.CENTER);
            reasonView.setPadding(0,0,0,UiFactory.dp(this,8));
            root.addView(reasonView);
            TextView hint=UiFactory.mutedText(this,"This attention message blocks the device until the parent removes the limitation.",14);
            hint.setGravity(Gravity.CENTER);
            root.addView(hint);
            Button ask=UiFactory.primaryButton(this,"Ask parent to unlock");
            root.addView(ask);
            ask.setOnClickListener(v->{recordAction("asked parent to unlock");});
        }else{
            LinearLayout row=new LinearLayout(this);
            row.setGravity(Gravity.CENTER);
            row.setOrientation(LinearLayout.HORIZONTAL);
            Button dismiss=UiFactory.primaryButton(this,full?"OK":"Dismiss");
            Button snooze=UiFactory.button(this,"Snooze 5 min");
            row.addView(dismiss,new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1));
            row.addView(snooze,new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1));
            root.addView(row);
            dismiss.setOnClickListener(v->{recordAction(full?"accepted":"dismissed");finish();});
            snooze.setOnClickListener(v->{recordAction("snoozed 5 min");finish();});
        }
        setContentView(root);
    }

    void recordAction(String action){
        String stamp=ChatStore.now();
        SharedPreferences.Editor ed=getSharedPreferences("p",0).edit();
        ed.putString("lastParentQuickMessage",emoji+" "+text+" at "+stamp);
        ed.putString("lastChildQuickAction",emoji+" "+text+" — "+action+" at "+stamp);
        ed.putString("lastParentQuickMessageSeen",action+" at "+stamp).apply();
        ChatStore.append(this,"Parent",emoji+" "+text,"quick");
        ChatStore.append(this,"Child",action,"quick_action");
    }
}
