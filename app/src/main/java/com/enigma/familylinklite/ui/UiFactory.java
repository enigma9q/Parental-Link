package com.enigma.familylinklite.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public final class UiFactory {
    private UiFactory() {}

    public interface UserMenuClick {
        void onClick(View v);
    }

    public static TextView text(Activity activity, String value, int sp) {
        TextView view = new TextView(activity);
        view.setText(value);
        view.setTextSize(sp);
        view.setPadding(18, 10, 18, 10);
        return view;
    }

    public static Button button(Activity activity, String value) {
        Button button = new Button(activity);
        button.setText(value);
        button.setAllCaps(false);
        return button;
    }

    public static EditText oneLine(Activity activity, String hint) {
        EditText editText = new EditText(activity);
        editText.setHint(hint);
        editText.setSingleLine(true);
        editText.setPadding(18, 10, 18, 10);
        return editText;
    }

    public static EditText multiLine(Activity activity, String hint) {
        EditText editText = new EditText(activity);
        editText.setHint(hint);
        editText.setSingleLine(false);
        editText.setMinLines(2);
        editText.setPadding(18, 10, 18, 10);
        return editText;
    }

    public static LinearLayout attachRoot(Activity activity) {
        ScrollView scrollView = new ScrollView(activity);
        scrollView.setFillViewport(false);
        scrollView.setFitsSystemWindows(true);
        scrollView.setBackgroundColor(Color.rgb(245, 249, 252));
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 64, 24, 88);
        root.setBackgroundColor(Color.rgb(245, 249, 252));
        scrollView.addView(root);
        activity.setContentView(scrollView);
        return root;
    }

    public static TextView addTopBar(Activity activity, LinearLayout root, SharedPreferences prefs, UserMenuClick userMenuClick) {
        LinearLayout bar = new LinearLayout(activity);
        bar.setOrientation(LinearLayout.HORIZONTAL);

        TextView name = text(activity, "FamilyLink Lite", 20);
        boolean attention = prefs.getBoolean("updateAvailable", false)
                || prefs.getBoolean("securityAttention", false)
                || prefs.getBoolean("versionAttention", false);
        Button user = button(activity, attention ? "👤•" : "👤");

        bar.addView(name, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        bar.addView(user);
        root.addView(bar);
        user.setOnClickListener(userMenuClick::onClick);
        return name;
    }
}
