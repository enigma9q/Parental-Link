package com.enigma.familylinklite.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public final class UiFactory {
    private UiFactory() {}

    public interface UserMenuClick { void onClick(View v); }
    public interface HomeClick { void onClick(View v); }

    public static boolean isDark(Activity a) {
        SharedPreferences p = a.getSharedPreferences("p", 0);
        String mode = p.getString("themeMode", "dark");
        if ("dark".equals(mode)) return true;
        if ("light".equals(mode)) return false;
        int night = a.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return night == Configuration.UI_MODE_NIGHT_YES;
    }

    public static int bg(Activity a) { return isDark(a) ? Color.rgb(7, 12, 17) : Color.rgb(246, 249, 253); }
    public static int panel(Activity a) { return isDark(a) ? Color.rgb(18, 24, 31) : Color.WHITE; }
    public static int panel2(Activity a) { return isDark(a) ? Color.rgb(24, 32, 42) : Color.rgb(230, 240, 252); }
    public static int textColor(Activity a) { return isDark(a) ? Color.rgb(242, 246, 250) : Color.rgb(18, 26, 34); }
    public static int mutedTextColor(Activity a) { return isDark(a) ? Color.rgb(172, 181, 190) : Color.rgb(82, 96, 112); }
    public static int border(Activity a) { return isDark(a) ? Color.rgb(43, 53, 65) : Color.rgb(210, 222, 235); }
    public static int menuBg(Activity a) { return isDark(a) ? Color.rgb(12, 17, 23) : Color.rgb(235, 242, 250); }
    public static int menuRow(Activity a) { return isDark(a) ? Color.rgb(22, 29, 38) : Color.WHITE; }
    public static int blue() { return Color.rgb(22, 122, 255); }
    public static int green() { return Color.rgb(48, 196, 82); }
    public static int red() { return Color.rgb(245, 80, 86); }

    public static GradientDrawable rounded(Activity a, int color, float radiusDp) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp(a, radiusDp));
        g.setStroke(1, border(a));
        return g;
    }

    public static int dp(Activity a, float v) { return (int)(v * a.getResources().getDisplayMetrics().density + 0.5f); }

    public static TextView text(Activity activity, String value, int sp) {
        TextView view = new TextView(activity);
        view.setText(value);
        view.setTextSize(sp);
        view.setTextColor(textColor(activity));
        view.setPadding(dp(activity, 14), dp(activity, 7), dp(activity, 14), dp(activity, 7));
        return view;
    }

    public static TextView mutedText(Activity activity, String value, int sp) {
        TextView view = text(activity, value, sp);
        view.setTextColor(mutedTextColor(activity));
        return view;
    }

    public static Button button(Activity activity, String value) {
        Button button = new Button(activity);
        button.setText(value);
        button.setAllCaps(false);
        button.setTextSize(14);
        button.setMinHeight(dp(activity, 48));
        button.setBackground(rounded(activity, panel2(activity), 14));
        button.setTextColor(isDark(activity) ? Color.rgb(230, 241, 255) : Color.rgb(25, 95, 170));
        button.setPadding(dp(activity, 10), dp(activity, 8), dp(activity, 10), dp(activity, 8));
        return button;
    }

    public static Button primaryButton(Activity activity, String value) {
        Button b = button(activity, value);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(blue());
        bg.setCornerRadius(dp(activity, 14));
        b.setBackground(bg);
        b.setTextColor(Color.WHITE);
        return b;
    }

    public static EditText oneLine(Activity activity, String hint) {
        EditText editText = new EditText(activity);
        editText.setHint(hint);
        editText.setSingleLine(true);
        editText.setTextColor(textColor(activity));
        editText.setHintTextColor(mutedTextColor(activity));
        editText.setPadding(dp(activity, 14), dp(activity, 8), dp(activity, 14), dp(activity, 8));
        return editText;
    }

    public static EditText multiLine(Activity activity, String hint) {
        EditText editText = oneLine(activity, hint);
        editText.setSingleLine(false);
        editText.setMinLines(2);
        return editText;
    }

    public static LinearLayout attachRoot(Activity activity) {
        ScrollView scrollView = new ScrollView(activity);
        scrollView.setFillViewport(false);
        scrollView.setFitsSystemWindows(true);
        scrollView.setBackgroundColor(bg(activity));
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(activity, 14), dp(activity, 42), dp(activity, 14), dp(activity, 28));
        root.setBackgroundColor(bg(activity));
        scrollView.addView(root);
        activity.setContentView(scrollView);
        applyBars(activity);
        return root;
    }

    public static LinearLayout attachFixedRoot(Activity activity) {
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(activity, 14), dp(activity, 42), dp(activity, 14), dp(activity, 28));
        root.setFitsSystemWindows(true);
        root.setBackgroundColor(bg(activity));
        activity.setContentView(root);
        applyBars(activity);
        return root;
    }

    public static void applyBars(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            activity.getWindow().setStatusBarColor(bg(activity));
            activity.getWindow().setNavigationBarColor(bg(activity));
        }
        if (Build.VERSION.SDK_INT >= 23) {
            int flags = activity.getWindow().getDecorView().getSystemUiVisibility();
            if (isDark(activity)) flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            else flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
        }
    }

    public static TextView addTopBar(Activity activity, LinearLayout root, SharedPreferences prefs, UserMenuClick userMenuClick) {
        return addTopBar(activity, root, prefs, userMenuClick, null);
    }

    public static TextView addTopBar(Activity activity, LinearLayout root, SharedPreferences prefs, UserMenuClick userMenuClick, HomeClick homeClick) {
        LinearLayout bar = new LinearLayout(activity);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(android.view.Gravity.CENTER_VERTICAL);
        bar.setPadding(0, 0, 0, dp(activity, 8));

        LinearLayout titleBox = new LinearLayout(activity);
        titleBox.setOrientation(LinearLayout.HORIZONTAL);
        titleBox.setGravity(android.view.Gravity.CENTER_VERTICAL);

        TextView mark = new TextView(activity);
        mark.setText("PL");
        mark.setTextSize(15);
        mark.setGravity(android.view.Gravity.CENTER);
        mark.setTextColor(Color.WHITE);
        mark.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        mark.setIncludeFontPadding(false);
        GradientDrawable markBg = new GradientDrawable();
        markBg.setShape(GradientDrawable.RECTANGLE);
        markBg.setCornerRadius(dp(activity, 9));
        markBg.setColor(blue());
        mark.setBackground(markBg);
        LinearLayout.LayoutParams markLp = new LinearLayout.LayoutParams(dp(activity, 36), dp(activity, 36));
        markLp.setMargins(0,0,dp(activity,10),0);
        titleBox.addView(mark, markLp);
        if (homeClick != null) {
            mark.setOnClickListener(homeClick::onClick);
            mark.setClickable(true);
        }

        TextView name = text(activity, "Parental-Link", 20);
        name.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        titleBox.addView(name, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        boolean attention = prefs.getBoolean("updateAvailable", false)
                || prefs.getBoolean("securityAttention", false)
                || prefs.getBoolean("versionAttention", false)
                || prefs.getBoolean("childUnlockRequestPending", false);
        Button user = button(activity, attention ? "Menu ●" : "Menu");
        user.setTextSize(14);
        user.setMinWidth(dp(activity, 86));
        user.setMinHeight(dp(activity, 44));

        bar.addView(titleBox, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        bar.addView(user, new LinearLayout.LayoutParams(dp(activity, 92), dp(activity, 46)));
        root.addView(bar);
        View divider = new View(activity);
        divider.setBackgroundColor(border(activity));
        root.addView(divider, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        user.setOnClickListener(userMenuClick::onClick);
        return name;
    }
}
