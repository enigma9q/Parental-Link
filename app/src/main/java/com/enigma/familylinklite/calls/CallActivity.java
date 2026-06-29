package com.enigma.familylinklite.calls;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.enigma.familylinklite.ui.UiFactory;

public class CallActivity extends Activity {
    public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        String mode = getIntent() != null ? getIntent().getStringExtra("mode") : "incoming";
        String parent = getIntent() != null ? getIntent().getStringExtra("parent") : "Parent";
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(36, 36, 36, 36);
        TextView title = UiFactory.text(this, "Parental-Link call", 26);
        title.setGravity(Gravity.CENTER);
        root.addView(title);
        TextView text = UiFactory.text(this, "incoming".equals(mode) ? parent + " is calling this device." : "Calling " + parent + "...", 18);
        text.setGravity(Gravity.CENTER);
        root.addView(text);
        TextView placeholder = UiFactory.text(this, "Audio call placeholder. Signalling UI is ready; live microphone streaming will be wired to the secure local channel next.", 14);
        placeholder.setGravity(Gravity.CENTER);
        root.addView(placeholder);
        Button accept = UiFactory.button(this, "Accept");
        Button decline = UiFactory.button(this, "Decline / End");
        root.addView(accept);
        root.addView(decline);
        accept.setOnClickListener(v -> text.setText("Call connected placeholder"));
        decline.setOnClickListener(v -> finish());
        setContentView(root);
    }
}
