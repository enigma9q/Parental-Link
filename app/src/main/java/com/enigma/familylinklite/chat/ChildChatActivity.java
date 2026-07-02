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

public class ChildChatActivity extends Activity {
    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        String parent=getIntent()!=null?getIntent().getStringExtra("parent"):"Parent Control"; if(parent==null||parent.length()==0)parent="Parent Control";
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(32,48,32,32);root.setBackgroundColor(Color.rgb(218,237,248));
        TextView title=UiFactory.text(this,"Chat with "+parent,24);title.setGravity(Gravity.CENTER);root.addView(title);
        SharedPreferences prefs=getSharedPreferences("p",0);
        root.addView(UiFactory.text(this,ChatStore.lastLines(this,10),15));
        String last=prefs.getString("lastParentChat","");
        if(last.length()>0)root.addView(UiFactory.text(this,"Last parent message: "+last,15));
        root.addView(UiFactory.text(this,"Send a preset reply or emoji. The parent can check it from the Chat screen.",15));
        String[] messages={"👍 OK","🍽️ I am hungry","👌 Coming","⏱️ 5 more minutes","📞 Call me","🆘 I need help","✅ I finished","🙏 Sorry"};
        GridLayout grid=new GridLayout(this);grid.setColumnCount(2);
        for(String m:messages){Button replyButton=UiFactory.button(this,m);grid.addView(replyButton,new ViewGroup.LayoutParams(getResources().getDisplayMetrics().widthPixels/2-42,100));replyButton.setOnClickListener(v->{String text=((Button)v).getText().toString();SharedPreferences.Editor ed=getSharedPreferences("p",0).edit();ed.putString("lastChildChatSent",text+" at "+ChatStore.now()).apply();ChatStore.append(this,"Child",text,"sent");Toast.makeText(this,"Saved for parent",Toast.LENGTH_SHORT).show();finish();});}
        root.addView(grid);
        Button close=UiFactory.button(this,"Close");root.addView(close);close.setOnClickListener(v->finish());
        setContentView(root);
    }
}
