package com.enigma.familylinklite.chat;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.*;
import com.enigma.familylinklite.ui.UiFactory;
import com.enigma.familylinklite.storage.ChatStore;

public class ChatMessageActivity extends Activity {
    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setGravity(Gravity.CENTER);root.setPadding(32,32,32,32);root.setBackgroundColor(Color.WHITE);
        TextView title=UiFactory.text(this,"💬\nMessage from parent",24);title.setGravity(Gravity.CENTER);root.addView(title);
        String msg=getIntent()!=null?getIntent().getStringExtra("message"):""; if(msg==null)msg="";
        String stamp=ChatStore.now();
        getSharedPreferences("p",0).edit().putString("lastParentChat",msg+" at "+stamp).putString("lastParentChatSeen","seen at "+stamp).apply();
        ChatStore.append(this,"Parent",msg,"seen");
        TextView body=UiFactory.text(this,msg,28);body.setGravity(Gravity.CENTER);body.setPadding(0,24,0,24);root.addView(body);
        TextView hint=UiFactory.text(this,"Reply with one tap.",14);hint.setGravity(Gravity.CENTER);root.addView(hint);
        String[] replies={"👍 OK","👌 Coming","🙏 Sorry","❤️","📞 Call me"};
        GridLayout grid=new GridLayout(this);grid.setColumnCount(2);
        for(String r:replies){Button btn=UiFactory.button(this,r);grid.addView(btn,new ViewGroup.LayoutParams(getResources().getDisplayMetrics().widthPixels/2-42,100));btn.setOnClickListener(v->{String reply=((Button)v).getText().toString();SharedPreferences.Editor ed=getSharedPreferences("p",0).edit();String t=ChatStore.now();ed.putString("lastChildChatReply",reply+" at "+t).putString("lastChildChatReplyTo",msg).putString("lastChildChatSent",reply+" at "+t).apply();ChatStore.append(this,"Child",reply,"reply");finish();});}
        root.addView(grid);
        Button ok=UiFactory.button(this,"OK");root.addView(ok);ok.setOnClickListener(v->{String t=ChatStore.now();getSharedPreferences("p",0).edit().putString("lastParentChatSeen","acknowledged at "+t).apply();finish();});
        setContentView(root);
    }
}
