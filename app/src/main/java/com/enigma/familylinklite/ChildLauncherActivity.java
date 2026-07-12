package com.enigma.familylinklite;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.enigma.familylinklite.ui.UiFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ChildLauncherActivity extends Activity {
    private PackageManager packageManager;
    private LinearLayout appList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageManager = getPackageManager();
        render();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (appList != null) {
            renderApps();
        }
    }

    private void render() {
        LinearLayout root = UiFactory.attachFixedRoot(this);
        root.setPadding(0, UiFactory.dp(this, 38), 0, UiFactory.dp(this, 18));

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        top.setPadding(UiFactory.dp(this, 18), 0, UiFactory.dp(this, 18), UiFactory.dp(this, 10));
        TextView title = UiFactory.text(this, "Apps", 24);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setPadding(0, 0, 0, 0);
        top.addView(title, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        TextView refresh = UiFactory.text(this, "\u21BB", 22);
        refresh.setGravity(Gravity.CENTER);
        refresh.setTextColor(UiFactory.blue());
        refresh.setBackground(UiFactory.actionShape(this, UiFactory.panel(this), 11, UiFactory.border(this)));
        top.addView(refresh, new LinearLayout.LayoutParams(UiFactory.dp(this, 50), UiFactory.dp(this, 46)));
        root.addView(top);

        TextView hint = UiFactory.mutedText(this, "Choose an app to open.", 14);
        hint.setPadding(UiFactory.dp(this, 18), 0, UiFactory.dp(this, 18), UiFactory.dp(this, 8));
        root.addView(hint);

        ScrollView scroll = new ScrollView(this);
        appList = new LinearLayout(this);
        appList.setOrientation(LinearLayout.VERTICAL);
        appList.setPadding(UiFactory.dp(this, 10), 0, UiFactory.dp(this, 10), UiFactory.dp(this, 16));
        scroll.addView(appList);
        root.addView(scroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        refresh.setOnClickListener(v -> renderApps());
        renderApps();
    }

    private void renderApps() {
        appList.removeAllViews();
        ArrayList<AppItem> apps = loadApps();
        if (apps.isEmpty()) {
            TextView empty = UiFactory.mutedText(this, "No child apps found.", 16);
            empty.setGravity(Gravity.CENTER);
            appList.addView(empty, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UiFactory.dp(this, 120)));
            return;
        }
        for (AppItem item : apps) {
            appList.addView(appRow(item));
            appList.addView(divider());
        }
    }

    private ArrayList<AppItem> loadApps() {
        ArrayList<AppItem> apps = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        Set<String> homes = homePackages();
        Intent launcher = new Intent(Intent.ACTION_MAIN);
        launcher.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolved = packageManager.queryIntentActivities(launcher, 0);
        if (resolved == null) return apps;
        for (ResolveInfo ri : resolved) {
            if (!shouldShow(ri, homes)) continue;
            String pkg = ri.activityInfo.packageName;
            if (!seen.add(pkg)) continue;
            Intent launch = packageManager.getLaunchIntentForPackage(pkg);
            if (launch == null) continue;
            CharSequence rawLabel = ri.loadLabel(packageManager);
            String label = rawLabel == null ? pkg : rawLabel.toString().trim();
            if (label.length() == 0) label = pkg;
            Drawable icon;
            try {
                icon = ri.loadIcon(packageManager);
            } catch (Exception e) {
                icon = getDrawable(R.mipmap.ic_launcher);
            }
            apps.add(new AppItem(label, pkg, icon, launch));
        }
        apps.sort((a, b) -> a.label.compareToIgnoreCase(b.label));
        return apps;
    }

    private boolean shouldShow(ResolveInfo ri, Set<String> homePackages) {
        if (ri == null || ri.activityInfo == null || ri.activityInfo.packageName == null) return false;
        String pkg = ri.activityInfo.packageName;
        if (pkg.equals(getPackageName())) return false;
        if (homePackages.contains(pkg)) return false;
        String lower = pkg.toLowerCase(Locale.US);
        if (lower.equals("com.android.settings") || lower.equals("com.samsung.android.settings")) return false;
        if (lower.contains(".settings") || lower.endsWith(".settings")) return false;
        if (lower.equals("android") || lower.startsWith("com.android.systemui")) return false;
        ApplicationInfo appInfo = ri.activityInfo.applicationInfo;
        if (appInfo == null) return false;
        boolean system = (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        boolean updatedSystem = (appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
        if (system && !updatedSystem) return false;
        return packageManager.getLaunchIntentForPackage(pkg) != null;
    }

    private Set<String> homePackages() {
        Set<String> out = new HashSet<>();
        try {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            List<ResolveInfo> infos = packageManager.queryIntentActivities(home, 0);
            if (infos != null) {
                for (ResolveInfo ri : infos) {
                    if (ri != null && ri.activityInfo != null && ri.activityInfo.packageName != null) {
                        out.add(ri.activityInfo.packageName);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return out;
    }

    private View appRow(AppItem item) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(UiFactory.dp(this, 14), UiFactory.dp(this, 10), UiFactory.dp(this, 14), UiFactory.dp(this, 10));
        row.setMinimumHeight(UiFactory.dp(this, 64));

        ImageView icon = new ImageView(this);
        icon.setImageDrawable(item.icon);
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        row.addView(icon, new LinearLayout.LayoutParams(UiFactory.dp(this, 48), UiFactory.dp(this, 48)));

        TextView name = UiFactory.text(this, item.label, 17);
        name.setTypeface(Typeface.DEFAULT_BOLD);
        name.setSingleLine(true);
        name.setPadding(UiFactory.dp(this, 14), 0, UiFactory.dp(this, 8), 0);
        row.addView(name, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        TextView open = UiFactory.text(this, "\u203A", 26);
        open.setGravity(Gravity.CENTER);
        open.setTextColor(UiFactory.blue());
        open.setPadding(0, 0, 0, 0);
        row.addView(open, new LinearLayout.LayoutParams(UiFactory.dp(this, 30), UiFactory.dp(this, 44)));

        row.setOnClickListener(v -> openApp(item));
        return row;
    }

    private View divider() {
        View line = new View(this);
        line.setBackgroundColor(UiFactory.border(this));
        line.setAlpha(0.8f);
        line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Math.max(1, UiFactory.dp(this, 1))));
        return line;
    }

    private void openApp(AppItem item) {
        try {
            Intent launch = new Intent(item.launch);
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launch);
        } catch (Exception e) {
            Toast.makeText(this, "Could not open " + item.label, Toast.LENGTH_SHORT).show();
        }
    }

    private static final class AppItem {
        final String label;
        final String packageName;
        final Drawable icon;
        final Intent launch;

        AppItem(String label, String packageName, Drawable icon, Intent launch) {
            this.label = label;
            this.packageName = packageName;
            this.icon = icon;
            this.launch = launch;
        }
    }
}
