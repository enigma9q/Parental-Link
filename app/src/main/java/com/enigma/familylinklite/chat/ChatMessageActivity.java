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
import com.enigma.familylinklite.storage.ChatStore;
import com.enigma.familylinklite.ui.UiFactory;

public class ChatMessageActivity extends Activity {
    LinearLayout history;
    String message;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        String raw = getIntent() != null ? getIntent().getStringExtra("message") : "";
        message = raw == null ? "" : raw.trim();
        if (message.length() == 0) message = "Message from parent";

        String stamp = ChatStore.now();
        getSharedPreferences("p", 0).edit()
                .putString("lastParentChat", message + " at " + stamp)
                .putString("lastParentChatSeen", "seen at " + stamp)
                .apply();
        ChatStore.append(this, "Parent", message, "seen");

        LinearLayout shell = UiFactory.attachFixedRoot(this);
        UiFactory.addTopBar(this, shell, getSharedPreferences("p", 0), v -> finish(), null, "Chat");

        TextView title = UiFactory.text(this, "Message from parent", 18);
        title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        title.setPadding(0, UiFactory.dp(this, 6), 0, UiFactory.dp(this, 4));
        shell.addView(title);

        ScrollView scroll = new ScrollView(this);
        history = new LinearLayout(this);
        history.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(history);
        shell.addView(scroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        renderHistory();

        TextView hint = UiFactory.mutedText(this, "Reply with one tap.", 13);
        hint.setPadding(0, UiFactory.dp(this, 8), 0, UiFactory.dp(this, 4));
        shell.addView(hint);

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(2);
        String[][] replies = {
                {"yes", "Yes"},
                {"clock", "In 5"},
                {"no", "No"},
                {"phone", "Call me"},
                {"chat", "OK"}
        };
        for (String[] r : replies) {
            Button button = UiFactory.button(this, r[1]);
            button.setTextSize(13);
            button.setCompoundDrawablesWithIntrinsicBounds(0, ChatIcons.res(r[0]), 0, 0);
            button.setCompoundDrawablePadding(UiFactory.dp(this, 5));
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = (getResources().getDisplayMetrics().widthPixels - UiFactory.dp(this, 44)) / 2;
            lp.height = UiFactory.dp(this, 72);
            lp.setMargins(UiFactory.dp(this, 3), UiFactory.dp(this, 3), UiFactory.dp(this, 3), UiFactory.dp(this, 3));
            grid.addView(button, lp);
            button.setOnClickListener(v -> reply(r[1]));
        }
        shell.addView(grid);
    }

    void renderHistory() {
        history.removeAllViews();
        for (String line : ChatStore.recentLines(this, 12)) history.addView(messageRow(line));
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
            a.setOnClickListener(v -> actionMenu());
        }
        bubble.setOnClickListener(v -> actionMenu());
        row.addView(bubble, new LinearLayout.LayoutParams((int)(getResources().getDisplayMetrics().widthPixels * 0.86f), ViewGroup.LayoutParams.WRAP_CONTENT));
        return row;
    }

    String cleanLine(String line) {
        return line.replace("  seen", "").replace("  sent", "").replace("  reply", "").replace("  quick", "").replace("  quick_action", "");
    }

    void actionMenu() {
        String[] choices = {"Reply OK", "Ask for 5 minutes", "Close"};
        new AlertDialog.Builder(this).setTitle("Message action").setItems(choices, (d, which) -> {
            if (which == 0) reply("Yes");
            else if (which == 1) reply("In 5");
            else finish();
        }).show();
    }

    void reply(String reply) {
        String t = ChatStore.now();
        SharedPreferences.Editor ed = getSharedPreferences("p", 0).edit();
        ed.putString("lastChildChatReply", reply + " at " + t)
                .putString("lastChildChatReplyTo", message)
                .putString("lastChildChatSent", reply + " at " + t)
                .apply();
        ChatStore.append(this, "Child", reply, "reply");
        finish();
    }
}
