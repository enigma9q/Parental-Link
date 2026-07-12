package com.enigma.familylinklite.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.enigma.familylinklite.storage.ChatStore;
import com.enigma.familylinklite.ui.UiFactory;

public class ChildChatActivity extends Activity {
    LinearLayout history;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        String parent = getIntent() != null ? getIntent().getStringExtra("parent") : "Parent Control";
        if (parent == null || parent.trim().length() == 0) parent = "Parent Control";

        LinearLayout shell = UiFactory.attachFixedRoot(this);
        UiFactory.addTopBar(this, shell, getSharedPreferences("p", 0), v -> finish(), null, "Chat");

        TextView title = UiFactory.text(this, "Chat with " + parent, 18);
        title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        title.setPadding(0, UiFactory.dp(this, 6), 0, UiFactory.dp(this, 4));
        shell.addView(title);

        ScrollView scroll = new ScrollView(this);
        history = new LinearLayout(this);
        history.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(history);
        shell.addView(scroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        renderHistory();

        TextView presets = UiFactory.mutedText(this, "Tap a message to send it to the parent.", 13);
        presets.setPadding(0, UiFactory.dp(this, 8), 0, UiFactory.dp(this, 4));
        shell.addView(presets);

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(2);
        String[][] messages = {
                {"yes", "Yes"},
                {"clock", "In 5"},
                {"no", "No"},
                {"food", "I am hungry"},
                {"phone", "Call me"},
                {"help", "I need help"},
                {"yes", "I finished"},
                {"chat", "Sorry"}
        };
        for (String[] m : messages) {
            Button button = UiFactory.button(this, m[1]);
            button.setTextSize(13);
            button.setCompoundDrawablesWithIntrinsicBounds(0, ChatIcons.res(m[0]), 0, 0);
            button.setCompoundDrawablePadding(UiFactory.dp(this, 5));
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = (getResources().getDisplayMetrics().widthPixels - UiFactory.dp(this, 44)) / 2;
            lp.height = UiFactory.dp(this, 72);
            lp.setMargins(UiFactory.dp(this, 3), UiFactory.dp(this, 3), UiFactory.dp(this, 3), UiFactory.dp(this, 3));
            grid.addView(button, lp);
            button.setOnClickListener(v -> sendToParent(m[1]));
        }
        shell.addView(grid);
    }

    void renderHistory() {
        history.removeAllViews();
        String[] rows = ChatStore.recentLines(this, 14);
        if (rows.length == 0) {
            TextView empty = UiFactory.mutedText(this, "No chat messages yet.", 15);
            empty.setGravity(Gravity.CENTER);
            history.addView(empty, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UiFactory.dp(this, 120)));
            return;
        }
        for (String line : rows) history.addView(messageRow(line));
    }

    View messageRow(String line) {
        boolean child = line.contains("  Child:");
        LinearLayout row = new LinearLayout(this);
        row.setGravity(child ? Gravity.RIGHT : Gravity.LEFT);
        row.setPadding(0, UiFactory.dp(this, 3), 0, UiFactory.dp(this, 3));

        LinearLayout bubble = new LinearLayout(this);
        bubble.setOrientation(LinearLayout.HORIZONTAL);
        bubble.setGravity(Gravity.CENTER_VERTICAL);
        bubble.setPadding(UiFactory.dp(this, 12), UiFactory.dp(this, 8), UiFactory.dp(this, 8), UiFactory.dp(this, 8));
        bubble.setBackground(UiFactory.actionShape(this, child ? UiFactory.panel2(this) : UiFactory.panel(this), 12, UiFactory.border(this)));

        TextView text = UiFactory.text(this, cleanLine(line), 14);
        text.setPadding(0, 0, UiFactory.dp(this, 8), 0);
        bubble.addView(text, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        String[] actions = {"\u21A9", "\u23F1", "\u2713"};
        for (String action : actions) {
            TextView a = UiFactory.text(this, action, 16);
            a.setGravity(Gravity.CENTER);
            bubble.addView(a, new LinearLayout.LayoutParams(UiFactory.dp(this, 30), UiFactory.dp(this, 30)));
            a.setOnClickListener(v -> chooseAction(line));
        }
        bubble.setOnClickListener(v -> chooseAction(line));
        row.addView(bubble, new LinearLayout.LayoutParams((int)(getResources().getDisplayMetrics().widthPixels * 0.86f), ViewGroup.LayoutParams.WRAP_CONTENT));
        return row;
    }

    String cleanLine(String line) {
        return line.replace("  seen", "").replace("  sent", "").replace("  reply", "").replace("  quick", "").replace("  quick_action", "");
    }

    void chooseAction(String line) {
        String[] choices = {"Reply OK", "Ask for 5 minutes", "Mark as read"};
        new AlertDialog.Builder(this).setTitle("Message action").setItems(choices, (d, which) -> {
            if (which == 0) sendToParent("Yes");
            else if (which == 1) sendToParent("In 5");
            else Toast.makeText(this, "Marked as read", Toast.LENGTH_SHORT).show();
        }).show();
    }

    void sendToParent(String text) {
        String t = ChatStore.now();
        SharedPreferences.Editor ed = getSharedPreferences("p", 0).edit();
        ed.putString("lastChildChatSent", text + " at " + t).apply();
        ChatStore.append(this, "Child", text, "sent");
        Toast.makeText(this, "Saved for parent", Toast.LENGTH_SHORT).show();
        renderHistory();
    }
}
