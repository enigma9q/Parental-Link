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
import android.widget.FrameLayout;
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

    public static GradientDrawable actionShape(Activity a, int color, float radiusDp, int strokeColor) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp(a, radiusDp));
        g.setStroke(dp(a, 1), strokeColor);
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
        button.setBackground(actionShape(activity, isDark(activity) ? Color.rgb(18, 25, 33) : Color.WHITE, 11, border(activity)));
        button.setTextColor(textColor(activity));
        button.setPadding(dp(activity, 10), dp(activity, 8), dp(activity, 10), dp(activity, 8));
        return button;
    }

    public static Button primaryButton(Activity activity, String value) {
        Button b = button(activity, value);
        b.setBackground(actionShape(activity, isDark(activity) ? Color.rgb(18, 25, 33) : Color.WHITE, 11, border(activity)));
        b.setTextColor(textColor(activity));
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
        LinearLayout shell = new LinearLayout(activity);
        shell.setOrientation(LinearLayout.VERTICAL);
        shell.setFitsSystemWindows(true);
        shell.setBackgroundColor(bg(activity));
        shell.setPadding(dp(activity, 14), dp(activity, 42), dp(activity, 14), dp(activity, 28));
        ScrollView scrollView = new ScrollView(activity);
        scrollView.setFillViewport(false);
        scrollView.setBackgroundColor(bg(activity));
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(0, dp(activity, 8), 0, 0);
        root.setBackgroundColor(bg(activity));
        scrollView.addView(root);
        shell.addView(scrollView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        activity.setContentView(shell);
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
        return addTopBar(activity, root, prefs, userMenuClick, null, "Parental-Link");
    }

    public static TextView addTopBar(Activity activity, LinearLayout root, SharedPreferences prefs, UserMenuClick userMenuClick, HomeClick homeClick) {
        return addTopBar(activity, root, prefs, userMenuClick, homeClick, "Parental-Link");
    }

    public static TextView addTopBar(Activity activity, LinearLayout root, SharedPreferences prefs, UserMenuClick userMenuClick, HomeClick homeClick, String title) {
        LinearLayout host = root;
        ViewGroup parent = (ViewGroup) root.getParent();
        if (parent instanceof ScrollView && parent.getParent() instanceof LinearLayout) {
            host = (LinearLayout) parent.getParent();
        }
        LinearLayout bar = new LinearLayout(activity);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(android.view.Gravity.CENTER_VERTICAL);
        bar.setPadding(0, 0, 0, dp(activity, 8));

        String screenTitle = title == null || title.trim().length() == 0 ? "Parental-Link" : title.trim();
        FrameLayout userBox = new FrameLayout(activity);
        Button user = button(activity, "\u2630");
        user.setTextSize(22);
        user.setMinWidth(dp(activity, 48));
        user.setMinHeight(dp(activity, 44));
        LinearLayout.LayoutParams userLp = new LinearLayout.LayoutParams(dp(activity, 52), dp(activity, 46));
        userLp.setMargins(0, 0, dp(activity, 10), 0);
        userBox.addView(user, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (hasAttention(prefs)) {
            TextView dot = new TextView(activity);
            dot.setText("");
            GradientDrawable badge = new GradientDrawable();
            badge.setShape(GradientDrawable.OVAL);
            badge.setColor(blue());
            dot.setBackground(badge);
            FrameLayout.LayoutParams dlp = new FrameLayout.LayoutParams(dp(activity, 9), dp(activity, 9), android.view.Gravity.RIGHT | android.view.Gravity.TOP);
            dlp.setMargins(0, dp(activity, 8), dp(activity, 8), 0);
            userBox.addView(dot, dlp);
        }
        bar.addView(userBox, userLp);

        TextView name = text(activity, screenTitle, 20);
        name.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        name.setSingleLine(true);
        bar.addView(name, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        host.addView(bar, 0);
        user.setOnClickListener(userMenuClick::onClick);
        return name;
    }

    static boolean hasAttention(SharedPreferences prefs) {
        return prefs.getBoolean("updateAvailable", false)
                || prefs.getBoolean("securityAttention", false)
                || prefs.getBoolean("versionAttention", false)
                || prefs.getBoolean("childUnlockRequestPending", false);
    }
}
