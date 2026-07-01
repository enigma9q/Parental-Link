package com.enigma.familylinklite.chat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.enigma.familylinklite.storage.ChatStore;
import com.enigma.familylinklite.ui.UiFactory;

public class QuickChatOverlayActivity extends Activity {
    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        String rawEmoji=getIntent()!=null?getIntent().getStringExtra("emoji"):"💬"; if(rawEmoji==null||rawEmoji.length()==0)rawEmoji="💬";
        String rawText=getIntent()!=null?getIntent().getStringExtra("text"):"Message from parent"; if(rawText==null)rawText="Message from parent";
        final String emoji=rawEmoji;
        final String text=rawText;
        getWindow().setStatusBarColor(Color.WHITE);
        LinearLayout root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(40,40,40,40);
        root.setBackgroundColor(Color.WHITE);

        TextView emojiView=UiFactory.text(this,emoji,72);
        emojiView.setGravity(Gravity.CENTER);
        root.addView(emojiView,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView body=UiFactory.text(this,text,28);
        body.setGravity(Gravity.CENTER);
        body.setPadding(0,16,0,24);
        root.addView(body,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));

        Button ok=UiFactory.button(this,"OK");
        root.addView(ok);
        ok.setOnClickListener(v->{
            String stamp=ChatStore.now();
            SharedPreferences.Editor ed=getSharedPreferences("p",0).edit();
            ed.putString("lastParentQuickMessage",emoji+" "+text+" at "+stamp);
            ed.putString("lastParentQuickMessageSeen","acknowledged at "+stamp).apply();
            ChatStore.append(this,"Parent",emoji+" "+text,"quick");
            finish();
        });

        setContentView(root);
    }
}
