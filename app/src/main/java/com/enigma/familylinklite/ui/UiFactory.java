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
import android.graphics.drawable.GradientDrawable;

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
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setTextColor(Color.rgb(25, 118, 210));
        button.setPadding(18, 10, 18, 10);
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
        scrollView.setBackgroundColor(Color.rgb(185, 215, 235));
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 72, 36, 88);
        root.setBackgroundColor(Color.rgb(185, 215, 235));
        scrollView.addView(root);
        activity.setContentView(scrollView);
        if (android.os.Build.VERSION.SDK_INT >= 21) { activity.getWindow().setStatusBarColor(Color.rgb(95, 145, 180)); activity.getWindow().setNavigationBarColor(Color.rgb(185, 215, 235)); }
        return root;
    }


    public static LinearLayout attachFixedRoot(Activity activity) {
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 72, 36, 88);
        root.setFitsSystemWindows(true);
        root.setBackgroundColor(Color.rgb(185, 215, 235));
        activity.setContentView(root);
        if (android.os.Build.VERSION.SDK_INT >= 21) { activity.getWindow().setStatusBarColor(Color.rgb(95, 145, 180)); activity.getWindow().setNavigationBarColor(Color.rgb(185, 215, 235)); }
        return root;
    }

    public static TextView addTopBar(Activity activity, LinearLayout root, SharedPreferences prefs, UserMenuClick userMenuClick) {
        LinearLayout bar = new LinearLayout(activity);
        bar.setOrientation(LinearLayout.HORIZONTAL);

        TextView name = text(activity, "Parental-Link", 20);
        boolean attention = prefs.getBoolean("updateAvailable", false)
                || prefs.getBoolean("securityAttention", false)
                || prefs.getBoolean("versionAttention", false);
        Button user = button(activity, attention ? "👤 ●" : "👤");
        user.setTextSize(26);
        GradientDrawable userBg = new GradientDrawable();
        userBg.setShape(GradientDrawable.OVAL);
        userBg.setColor(Color.WHITE);
        userBg.setStroke(1, Color.rgb(210, 225, 235));
        user.setBackground(userBg);
        user.setMinWidth(96);
        user.setMinHeight(96);

        bar.addView(name, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        LinearLayout.LayoutParams userLp = new LinearLayout.LayoutParams(96,96);
        userLp.setMargins(0,0,24,0);
        bar.addView(user, userLp);
        root.addView(bar);
        View divider = new View(activity);
        divider.setBackgroundColor(Color.rgb(120, 165, 195));
        root.addView(divider, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        user.setOnClickListener(userMenuClick::onClick);
        return name;
    }
}
