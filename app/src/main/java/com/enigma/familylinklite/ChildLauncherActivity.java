package com.enigma.familylinklite;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
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
    private TextView countView;
    private EditText searchBox;
    private LinearLayout folderStrip;
    private ArrayList<AppItem> allApps = new ArrayList<>();
    private String selectedFolder = "All";

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
        root.setPadding(UiFactory.dp(this, 12), UiFactory.dp(this, 38), UiFactory.dp(this, 12), UiFactory.dp(this, 18));

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(UiFactory.dp(this, 12), UiFactory.dp(this, 12), UiFactory.dp(this, 12), UiFactory.dp(this, 12));
        header.setBackground(UiFactory.rounded(this, UiFactory.panel2(this), 18));
        root.addView(header, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        SharedPreferences prefs = getSharedPreferences("p", 0);
        TextView avatar = UiFactory.text(this, prefs.getString("childIcon", "\uD83D\uDCFA"), 30);
        avatar.setGravity(Gravity.CENTER);
        avatar.setIncludeFontPadding(false);
        avatar.setBackground(UiFactory.actionShape(this, UiFactory.panel(this), 14, UiFactory.border(this)));
        top.addView(avatar, new LinearLayout.LayoutParams(UiFactory.dp(this, 58), UiFactory.dp(this, 58)));

        LinearLayout titleBox = new LinearLayout(this);
        titleBox.setOrientation(LinearLayout.VERTICAL);
        TextView title = UiFactory.text(this, "Child apps", 24);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setPadding(0, 0, 0, 0);
        countView = UiFactory.mutedText(this, "Loading apps", 13);
        countView.setPadding(0, UiFactory.dp(this, 2), 0, 0);
        titleBox.addView(title);
        titleBox.addView(countView);
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        tlp.setMargins(UiFactory.dp(this, 12), 0, UiFactory.dp(this, 8), 0);
        top.addView(titleBox, tlp);

        TextView refresh = UiFactory.text(this, "\u21BB", 22);
        refresh.setGravity(Gravity.CENTER);
        refresh.setTextColor(UiFactory.blue());
        refresh.setBackground(UiFactory.actionShape(this, UiFactory.panel(this), 11, UiFactory.border(this)));
        top.addView(refresh, new LinearLayout.LayoutParams(UiFactory.dp(this, 50), UiFactory.dp(this, 46)));
        header.addView(top);

        searchBox = UiFactory.oneLine(this, "Search apps");
        searchBox.setSingleLine(true);
        searchBox.setTextSize(15);
        searchBox.setBackground(UiFactory.actionShape(this, UiFactory.panel(this), 12, UiFactory.border(this)));
        searchBox.setPadding(UiFactory.dp(this, 14), 0, UiFactory.dp(this, 14), 0);
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UiFactory.dp(this, 46));
        slp.setMargins(0, UiFactory.dp(this, 12), 0, 0);
        header.addView(searchBox, slp);

        TextView desktopLabel = UiFactory.mutedText(this, "Desktop", 12);
        desktopLabel.setTypeface(Typeface.DEFAULT_BOLD);
        desktopLabel.setPadding(0, UiFactory.dp(this, 10), 0, UiFactory.dp(this, 4));
        header.addView(desktopLabel);

        LinearLayout shortcuts = new LinearLayout(this);
        shortcuts.setOrientation(LinearLayout.HORIZONTAL);
        ButtonLike dashboard = new ButtonLike("Parental-Link", "ic_menu_dashboard");
        shortcuts.addView(shortcut(dashboard), new LinearLayout.LayoutParams(0, UiFactory.dp(this, 44), 1));
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UiFactory.dp(this, 44));
        header.addView(shortcuts, clp);

        TextView foldersLabel = UiFactory.mutedText(this, "Folders", 12);
        foldersLabel.setTypeface(Typeface.DEFAULT_BOLD);
        foldersLabel.setPadding(0, UiFactory.dp(this, 10), 0, UiFactory.dp(this, 4));
        header.addView(foldersLabel);

        HorizontalScrollView folderScroll = new HorizontalScrollView(this);
        folderScroll.setHorizontalScrollBarEnabled(false);
        folderStrip = new LinearLayout(this);
        folderStrip.setOrientation(LinearLayout.HORIZONTAL);
        folderScroll.addView(folderStrip);
        header.addView(folderScroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UiFactory.dp(this, 44)));

        TextView drawerLabel = UiFactory.mutedText(this, "App drawer", 12);
        drawerLabel.setTypeface(Typeface.DEFAULT_BOLD);
        drawerLabel.setPadding(UiFactory.dp(this, 4), UiFactory.dp(this, 10), 0, 0);
        root.addView(drawerLabel);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(false);
        scroll.setBackgroundColor(UiFactory.bg(this));
        appList = new LinearLayout(this);
        appList.setOrientation(LinearLayout.VERTICAL);
        appList.setPadding(0, UiFactory.dp(this, 10), 0, UiFactory.dp(this, 16));
        scroll.addView(appList);
        root.addView(scroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        refresh.setOnClickListener(v -> renderApps());
        searchBox.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { renderFilteredApps(); }
            public void afterTextChanged(Editable s) {}
        });
        renderApps();
    }

    private void renderApps() {
        allApps = loadApps();
        renderFolders();
        renderFilteredApps();
    }

    private void renderFilteredApps() {
        appList.removeAllViews();
        String query = searchBox == null ? "" : searchBox.getText().toString().trim().toLowerCase(Locale.US);
        ArrayList<AppItem> apps = new ArrayList<>();
        for (AppItem item : allApps) {
            boolean folderOk = "All".equals(selectedFolder) || item.category.equals(selectedFolder);
            boolean queryOk = query.length() == 0 || item.label.toLowerCase(Locale.US).contains(query);
            if (folderOk && queryOk) apps.add(item);
        }
        if (countView != null) {
            String suffix = allApps.size() == 1 ? "app available" : "apps available";
            countView.setText(selectedFolder + " - " + apps.size() + " of " + allApps.size() + " " + suffix);
        }
        if (apps.isEmpty()) {
            TextView empty = UiFactory.mutedText(this, query.length() == 0 ? "No apps in this folder yet." : "No apps match this search.", 16);
            empty.setGravity(Gravity.CENTER);
            appList.addView(empty, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UiFactory.dp(this, 120)));
            return;
        }
        for (AppItem item : apps) {
            appList.addView(appRow(item));
        }
    }

    private void renderFolders() {
        if (folderStrip == null) return;
        folderStrip.removeAllViews();
        String[] folders = new String[]{"All", "Games", "Education", "Video", "Social", "Browser", "Tools", "Other"};
        for (String folder : folders) {
            int count = "All".equals(folder) ? allApps.size() : countFolder(folder);
            if (!"All".equals(folder) && count == 0) continue;
            TextView chip = UiFactory.text(this, folder + "  " + count, 13);
            chip.setGravity(Gravity.CENTER);
            chip.setTypeface(Typeface.DEFAULT_BOLD);
            chip.setSingleLine(true);
            chip.setTextColor(folder.equals(selectedFolder) ? Color.WHITE : UiFactory.textColor(this));
            chip.setBackground(UiFactory.actionShape(this, folder.equals(selectedFolder) ? UiFactory.blue() : UiFactory.panel(this), 999, UiFactory.border(this)));
            chip.setPadding(UiFactory.dp(this, 12), 0, UiFactory.dp(this, 12), 0);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, UiFactory.dp(this, 36));
            lp.setMargins(0, 0, UiFactory.dp(this, 8), 0);
            folderStrip.addView(chip, lp);
            chip.setOnClickListener(v -> {
                selectedFolder = folder;
                renderFolders();
                renderFilteredApps();
            });
        }
    }

    private int countFolder(String folder) {
        int count = 0;
        for (AppItem item : allApps) {
            if (folder.equals(item.category)) count++;
        }
        return count;
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
            apps.add(new AppItem(label, pkg, icon, launch, categoryFor(ri.activityInfo.applicationInfo, label, pkg)));
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

    private String categoryFor(ApplicationInfo appInfo, String label, String pkg) {
        String haystack = ((label == null ? "" : label) + " " + (pkg == null ? "" : pkg)).toLowerCase(Locale.US);
        if (appInfo != null) {
            if (appInfo.category == ApplicationInfo.CATEGORY_GAME) return "Games";
            if (appInfo.category == ApplicationInfo.CATEGORY_VIDEO || appInfo.category == ApplicationInfo.CATEGORY_AUDIO) return "Video";
            if (appInfo.category == ApplicationInfo.CATEGORY_SOCIAL) return "Social";
            if (appInfo.category == ApplicationInfo.CATEGORY_PRODUCTIVITY) return "Tools";
        }
        if (haystack.contains("game") || haystack.contains("play") || haystack.contains("roblox") || haystack.contains("minecraft")) return "Games";
        if (haystack.contains("learn") || haystack.contains("school") || haystack.contains("classroom") || haystack.contains("duolingo") || haystack.contains("education")) return "Education";
        if (haystack.contains("youtube") || haystack.contains("video") || haystack.contains("netflix") || haystack.contains("tiktok") || haystack.contains("player")) return "Video";
        if (haystack.contains("chat") || haystack.contains("messenger") || haystack.contains("discord") || haystack.contains("whatsapp") || haystack.contains("social")) return "Social";
        if (haystack.contains("browser") || haystack.contains("chrome") || haystack.contains("firefox") || haystack.contains("edge")) return "Browser";
        if (haystack.contains("calculator") || haystack.contains("clock") || haystack.contains("calendar") || haystack.contains("files") || haystack.contains("docs")) return "Tools";
        return "Other";
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
        row.setPadding(UiFactory.dp(this, 12), UiFactory.dp(this, 10), UiFactory.dp(this, 12), UiFactory.dp(this, 10));
        row.setMinimumHeight(UiFactory.dp(this, 76));
        row.setBackground(UiFactory.actionShape(this, UiFactory.panel(this), 14, UiFactory.border(this)));

        ImageView icon = new ImageView(this);
        icon.setImageDrawable(item.icon);
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        icon.setBackground(UiFactory.actionShape(this, UiFactory.panel2(this), 14, UiFactory.border(this)));
        icon.setPadding(UiFactory.dp(this, 8), UiFactory.dp(this, 8), UiFactory.dp(this, 8), UiFactory.dp(this, 8));
        row.addView(icon, new LinearLayout.LayoutParams(UiFactory.dp(this, 56), UiFactory.dp(this, 56)));

        LinearLayout textBox = new LinearLayout(this);
        textBox.setOrientation(LinearLayout.VERTICAL);
        TextView name = UiFactory.text(this, item.label, 17);
        name.setTypeface(Typeface.DEFAULT_BOLD);
        name.setSingleLine(true);
        name.setPadding(0, 0, 0, 0);
        TextView folder = UiFactory.mutedText(this, item.category, 12);
        folder.setPadding(0, UiFactory.dp(this, 2), 0, 0);
        textBox.addView(name);
        textBox.addView(folder);
        LinearLayout.LayoutParams textLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        textLp.setMargins(UiFactory.dp(this, 14), 0, UiFactory.dp(this, 8), 0);
        row.addView(textBox, textLp);

        TextView open = UiFactory.text(this, "\u203A", 26);
        open.setGravity(Gravity.CENTER);
        open.setTextColor(UiFactory.blue());
        open.setPadding(0, 0, 0, 0);
        row.addView(open, new LinearLayout.LayoutParams(UiFactory.dp(this, 30), UiFactory.dp(this, 44)));

        row.setOnClickListener(v -> openApp(item));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, UiFactory.dp(this, 8));
        row.setLayoutParams(lp);
        return row;
    }

    private View shortcut(ButtonLike data) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        row.setPadding(UiFactory.dp(this, 8), 0, UiFactory.dp(this, 8), 0);
        row.setBackground(UiFactory.actionShape(this, UiFactory.panel(this), 11, UiFactory.border(this)));
        ImageView icon = new ImageView(this);
        int res = getResources().getIdentifier(data.drawable, "drawable", getPackageName());
        if (res != 0) icon.setImageResource(res);
        icon.setColorFilter(UiFactory.blue());
        row.addView(icon, new LinearLayout.LayoutParams(UiFactory.dp(this, 24), UiFactory.dp(this, 24)));
        TextView label = UiFactory.text(this, data.label, 14);
        label.setTypeface(Typeface.DEFAULT_BOLD);
        label.setPadding(UiFactory.dp(this, 8), 0, 0, 0);
        row.addView(label);
        row.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("open", "child_dashboard");
            startActivity(intent);
        });
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
        final String category;

        AppItem(String label, String packageName, Drawable icon, Intent launch, String category) {
            this.label = label;
            this.packageName = packageName;
            this.icon = icon;
            this.launch = launch;
            this.category = category;
        }
    }

    private static final class ButtonLike {
        final String label;
        final String drawable;

        ButtonLike(String label, String drawable) {
            this.label = label;
            this.drawable = drawable;
        }
    }
}
