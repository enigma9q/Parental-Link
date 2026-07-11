package com.enigma.familylinklite;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.enigma.familylinklite.services.ChildServerService;

public class AttentionActivity extends Activity {
    boolean blocking = false;
    boolean closing = false;
    BroadcastReceiver closeReceiver;
    TextView countdownView;
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable refocusRunnable = new Runnable() {
        public void run() {
            if (blocking && !closing && activeChildLock()) bringLockScreenToFront();
        }
    };
    Runnable countdownRunnable = new Runnable() {
        public void run() {
            updateCountdown();
        }
    };

    public void onCreate(Bundle b) {
        super.onCreate(b);
        closeReceiver = new BroadcastReceiver() {
            public void onReceive(Context c, Intent i) {
                showUnlockedAndReturnHome();
            }
        };
        IntentFilterCompat.register(this, closeReceiver, "com.enigma.familylinklite.CLOSE_ATTENTION");
        if (getIntent() != null && getIntent().getBooleanExtra("unlocked", false)) {
            showUnlockedAndReturnHome();
            return;
        }
        applyIntent(getIntent());
        buildScreen();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null && intent.getBooleanExtra("unlocked", false)) {
            showUnlockedAndReturnHome();
            return;
        }
        applyIntent(intent);
        buildScreen();
    }

    void applyIntent(Intent intent) {
        blocking = intent != null && intent.getBooleanExtra("blocking", false);
    }

    void buildScreen() {
        handler.removeCallbacks(countdownRunnable);
        countdownView = null;
        String title = getIntent().getStringExtra("title");
        String text = getIntent().getStringExtra("text");
        if (title == null) title = getString(R.string.app_name);
        if (text == null) text = getString(R.string.parent_action_attention);
        keepFrontVisuals();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(40, 40, 40, 40);
        root.setBackgroundColor(Color.rgb(245, 248, 255));

        TextView overlayIcon = new TextView(this);
        overlayIcon.setText(iconFor(title));
        overlayIcon.setTextSize(56);
        overlayIcon.setGravity(Gravity.CENTER);
        root.addView(overlayIcon, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView t = new TextView(this);
        t.setText(title);
        t.setTextSize(30);
        t.setTextColor(Color.BLACK);
        t.setGravity(Gravity.CENTER);
        t.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        root.addView(t, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView msg = new TextView(this);
        msg.setText(text);
        msg.setTextSize(20);
        msg.setTextColor(Color.BLACK);
        msg.setGravity(Gravity.CENTER);
        msg.setPadding(0, 18, 0, 18);
        root.addView(msg, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (blocking && isTimeoutScreen(title)) addCountdown(root);
        if (blocking) addBlockingControls(root, title);
        else {
            Button ok = new Button(this);
            ok.setAllCaps(false);
            ok.setText(getString(R.string.ok));
            root.addView(ok);
            ok.setOnClickListener(v -> finish());
        }
        setContentView(root);
    }

    void addCountdown(LinearLayout root) {
        countdownView = new TextView(this);
        countdownView.setTextSize(48);
        countdownView.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        countdownView.setTextColor(Color.rgb(20, 122, 255));
        countdownView.setGravity(Gravity.CENTER);
        countdownView.setPadding(0, 6, 0, 14);
        root.addView(countdownView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        updateCountdown();
    }

    void updateCountdown() {
        if (countdownView == null || closing) return;
        long remaining = getSharedPreferences("rules", 0).getLong("lock_until", 0) - System.currentTimeMillis();
        if (remaining <= 0) {
            showUnlockedAndReturnHome();
            return;
        }
        long totalSeconds = Math.max(1, (remaining + 999) / 1000);
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        if (hours > 0) countdownView.setText(String.format(java.util.Locale.US, "%d:%02d:%02d", hours, minutes, seconds));
        else countdownView.setText(String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds));
        handler.postDelayed(countdownRunnable, 1000);
    }

    boolean isTimeoutScreen(String title) {
        String s = title == null ? "" : title.toLowerCase(java.util.Locale.US);
        return s.contains("timeout");
    }

    void addBlockingControls(LinearLayout root, String title) {
        TextView hint = new TextView(this);
        hint.setText(getString(R.string.blocking_hint));
        hint.setTextSize(15);
        hint.setTextColor(Color.DKGRAY);
        hint.setGravity(Gravity.CENTER);
        hint.setPadding(0, 8, 0, 18);
        root.addView(hint, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Button ask = new Button(this);
        ask.setAllCaps(false);
        ask.setText(getString(R.string.ask_parent));
        ask.setTextSize(18);
        ask.setPadding(24, 16, 24, 16);
        root.addView(ask, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        long snoozeUntil = getSharedPreferences("rules", 0).getLong("ask_parent_snooze_until", 0);
        long now = System.currentTimeMillis();
        if (snoozeUntil > now) {
            ask.setEnabled(false);
            ask.setText("Ask again in " + Math.max(1, (snoozeUntil - now + 59999) / 60000) + " min");
        }
        final String reason = getIntent().getStringExtra("reason") != null ? getIntent().getStringExtra("reason") : title;
        ask.setOnClickListener(v -> {
            Intent si = new Intent(this, ChildServerService.class);
            si.setAction("ASK_PARENT_UNLOCK");
            si.putExtra("reason", reason);
            startService(si);
            ask.setEnabled(false);
            ask.setText(getString(R.string.request_sent));
        });
    }

    String iconFor(String title) {
        String s = title == null ? "" : title.toLowerCase(java.util.Locale.US);
        if (s.contains("ping")) return "\uD83D\uDCE2";
        if (s.contains("timeout")) return "\u23F1";
        if (s.contains("disabled")) return "\uD83D\uDD12";
        if (s.contains("blocked")) return "\uD83D\uDEAB";
        if (s.contains("call")) return "\uD83D\uDCDE";
        if (s.contains("message") || s.contains("chat")) return "\uD83D\uDCAC";
        return "\u2139\uFE0F";
    }

    void showUnlockedAndReturnHome() {
        closing = true;
        handler.removeCallbacksAndMessages(null);
        keepFrontVisuals();
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(40, 40, 40, 40);
        root.setBackgroundColor(Color.rgb(245, 248, 255));

        TextView icon = new TextView(this);
        icon.setText("\uD83D\uDD13");
        icon.setTextSize(64);
        icon.setGravity(Gravity.CENTER);
        root.addView(icon, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView title = new TextView(this);
        title.setText("Unlocked");
        title.setTextSize(30);
        title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        root.addView(title, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView msg = new TextView(this);
        msg.setText("Returning to home screen");
        msg.setTextSize(18);
        msg.setTextColor(Color.DKGRAY);
        msg.setGravity(Gravity.CENTER);
        msg.setPadding(0, 18, 0, 0);
        root.addView(msg, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setContentView(root);
        handler.postDelayed(() -> returnToLauncher(), 1000);
    }

    void returnToLauncher() {
        try {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(home);
        } catch (Exception ignored) {}
        finish();
    }

    void keepFrontVisuals() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        if (Build.VERSION.SDK_INT >= 27) getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.rgb(245, 248, 255));
            getWindow().setNavigationBarColor(Color.rgb(245, 248, 255));
        }
    }

    boolean activeChildLock() {
        return getSharedPreferences("rules", 0).getLong("lock_until", 0) > System.currentTimeMillis();
    }

    void bringLockScreenToFront() {
        try {
            Intent i = new Intent(this, AttentionActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            i.putExtra("title", getIntent().getStringExtra("title"));
            i.putExtra("text", getIntent().getStringExtra("text"));
            i.putExtra("reason", getIntent().getStringExtra("reason"));
            i.putExtra("blocking", true);
            startActivity(i);
        } catch (Exception ignored) {}
    }

    void scheduleRefocus(long delay) {
        handler.removeCallbacks(refocusRunnable);
        if (blocking && !closing && activeChildLock()) handler.postDelayed(refocusRunnable, delay);
    }

    protected void onResume() {
        super.onResume();
        handler.removeCallbacks(refocusRunnable);
        if (blocking && !activeChildLock()) {
            showUnlockedAndReturnHome();
            return;
        }
        keepFrontVisuals();
        if (blockingInSplitScreen()) scheduleRefocus(80);
    }

    protected void onPause() {
        super.onPause();
        scheduleRefocus(180);
    }

    protected void onStop() {
        super.onStop();
        scheduleRefocus(350);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) keepFrontVisuals();
        else scheduleRefocus(250);
        if (blockingInSplitScreen()) scheduleRefocus(80);
    }

    public void onMultiWindowModeChanged(boolean isInMultiWindowMode, Configuration newConfig) {
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig);
        if (isInMultiWindowMode && blocking && !closing && activeChildLock()) scheduleRefocus(60);
    }

    boolean blockingInSplitScreen() {
        return Build.VERSION.SDK_INT >= 24 && blocking && !closing && activeChildLock() && isInMultiWindowMode();
    }

    public void onBackPressed() {
        if (!blocking) super.onBackPressed();
    }

    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        try {
            if (closeReceiver != null) unregisterReceiver(closeReceiver);
        } catch (Exception ignored) {}
        super.onDestroy();
    }

    static final class IntentFilterCompat {
        static void register(Activity activity, BroadcastReceiver receiver, String action) {
            IntentFilter filter = new IntentFilter(action);
            if (Build.VERSION.SDK_INT >= 33) activity.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
            else activity.registerReceiver(receiver, filter);
        }
    }
}
