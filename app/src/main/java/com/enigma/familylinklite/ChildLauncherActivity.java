package com.enigma.familylinklite;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
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
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.enigma.familylinklite.ui.UiFactory;
import com.enigma.familylinklite.AdminReceiver;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ChildLauncherActivity extends Activity {
    private PackageManager packageManager;
    private LinearLayout appList;
    private TextView countView;
    private EditText searchBox;
    private LinearLayout folderStrip;
    private LinearLayout statusPanel;
    private ArrayList<AppItem> allApps = new ArrayList<>();
    private String selectedFolder = "All";
    private String sortMode = "name";
    private boolean compactRows = false;
    private boolean drawerOpen = false;
    private float swipeStartY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageManager = getPackageManager();
        compactRows = getSharedPreferences("launcher", 0).getBoolean("compactRows", false);
        render();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tryStartLockTaskIfAllowed();
        if (appList != null) {
            renderApps();
        }
    }

    private void render() {
        LinearLayout root = UiFactory.attachFixedRoot(this);
        boolean wide = isWideLandscape();
        if (!drawerOpen) {
            renderDesktop(root, wide);
            return;
        }
        root.setOrientation(wide ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
        root.setPadding(UiFactory.dp(this, 12), UiFactory.dp(this, 38), UiFactory.dp(this, 12), UiFactory.dp(this, 18));

        LinearLayout sidePane = root;
        LinearLayout drawerPane = root;
        if (wide) {
            sidePane = new LinearLayout(this);
            sidePane.setOrientation(LinearLayout.VERTICAL);
            sidePane.setPadding(0, 0, UiFactory.dp(this, 10), 0);
            drawerPane = new LinearLayout(this);
            drawerPane.setOrientation(LinearLayout.VERTICAL);
            root.addView(sidePane, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            root.addView(drawerPane, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 3));
        }

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(UiFactory.dp(this, 12), UiFactory.dp(this, 12), UiFactory.dp(this, 12), UiFactory.dp(this, 12));
        header.setBackground(UiFactory.rounded(this, UiFactory.panel2(this), 18));
        sidePane.addView(header, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, wide ? 0 : ViewGroup.LayoutParams.WRAP_CONTENT, wide ? 1 : 0));

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
        TextView home = UiFactory.text(this, "\u2302", 22);
        home.setGravity(Gravity.CENTER);
        home.setTextColor(UiFactory.blue());
        home.setBackground(UiFactory.actionShape(this, UiFactory.panel(this), 11, UiFactory.border(this)));
        LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(UiFactory.dp(this, 50), UiFactory.dp(this, 46));
        hlp.setMargins(UiFactory.dp(this, 8), 0, 0, 0);
        top.addView(home, hlp);
        header.addView(top);

        statusPanel = new LinearLayout(this);
        statusPanel.setOrientation(LinearLayout.VERTICAL);
        statusPanel.setPadding(0, UiFactory.dp(this, 8), 0, UiFactory.dp(this, 2));
        header.addView(statusPanel);
        renderStatusPanel();

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
        ButtonLike dashboard = new ButtonLike("Parental-Link", "ic_menu_dashboard", "dashboard");
        ButtonLike chat = new ButtonLike("Chat", "ic_proto_chat", "chat");
        shortcuts.addView(shortcut(dashboard), new LinearLayout.LayoutParams(0, UiFactory.dp(this, 44), 1));
        LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(UiFactory.dp(this, 8), 1);
        View spacer = new View(this);
        shortcuts.addView(spacer, gap);
        shortcuts.addView(shortcut(chat), new LinearLayout.LayoutParams(0, UiFactory.dp(this, 44), 1));
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

        LinearLayout controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.HORIZONTAL);
        TextView sort = controlChip("Sort: Name");
        TextView view = controlChip("View: List");
        controls.addView(sort, new LinearLayout.LayoutParams(0, UiFactory.dp(this, 40), 1));
        View controlGap = new View(this);
        controls.addView(controlGap, new LinearLayout.LayoutParams(UiFactory.dp(this, 8), 1));
        controls.addView(view, new LinearLayout.LayoutParams(0, UiFactory.dp(this, 40), 1));
        header.addView(controls);

        TextView drawerLabel = UiFactory.mutedText(this, "App drawer", 12);
        drawerLabel.setTypeface(Typeface.DEFAULT_BOLD);
        drawerLabel.setPadding(UiFactory.dp(this, 4), UiFactory.dp(this, 10), 0, 0);
        drawerPane.addView(drawerLabel);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(false);
        scroll.setBackgroundColor(UiFactory.bg(this));
        appList = new LinearLayout(this);
        appList.setOrientation(LinearLayout.VERTICAL);
        appList.setPadding(0, UiFactory.dp(this, 10), 0, UiFactory.dp(this, 16));
        scroll.addView(appList);
        drawerPane.addView(scroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        refresh.setOnClickListener(v -> renderApps());
        home.setOnClickListener(v -> closeDrawer());
        sort.setOnClickListener(v -> {
            sortMode = "name".equals(sortMode) ? "recent" : "name";
            sort.setText("Sort: " + ("recent".equals(sortMode) ? "Recent" : "Name"));
            renderFilteredApps();
        });
        view.setOnClickListener(v -> {
            compactRows = !compactRows;
            view.setText("View: " + (compactRows ? "Compact" : "List"));
            renderFilteredApps();
        });
        searchBox.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { renderFilteredApps(); }
            public void afterTextChanged(Editable s) {}
        });
        renderApps();
    }

    private boolean isWideLandscape() {
        return getResources().getConfiguration().screenWidthDp >= 700
                && getResources().getConfiguration().screenWidthDp > getResources().getConfiguration().screenHeightDp;
    }

    private void renderDesktop(LinearLayout root, boolean wide) {
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(UiFactory.dp(this, 14), UiFactory.dp(this, 38), UiFactory.dp(this, 14), UiFactory.dp(this, 14));
        root.setOnTouchListener((v, event) -> {
            if (!drawerSwipeEnabled()) return false;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                swipeStartY = event.getY();
                return true;
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (swipeStartY - event.getY() > UiFactory.dp(this, 80)) {
                    openDrawer();
                    return true;
                }
            }
            return true;
        });

        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView child = UiFactory.text(this, getSharedPreferences("p", 0).getString("childIcon", "\uD83D\uDCFA"), 28);
        child.setGravity(Gravity.CENTER);
        child.setBackground(UiFactory.actionShape(this, UiFactory.panel2(this), 14, UiFactory.border(this)));
        top.addView(child, new LinearLayout.LayoutParams(UiFactory.dp(this, 56), UiFactory.dp(this, 56)));
        LinearLayout titleBox = new LinearLayout(this);
        titleBox.setOrientation(LinearLayout.VERTICAL);
        TextView title = UiFactory.text(this, "Home", 24);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setPadding(0, 0, 0, 0);
        TextView hint = UiFactory.mutedText(this, drawerSwipeEnabled() ? "Swipe up for apps" : "Open apps from the drawer button", 13);
        hint.setPadding(0, 0, 0, 0);
        titleBox.addView(title);
        titleBox.addView(hint);
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        tlp.setMargins(UiFactory.dp(this, 12), 0, 0, 0);
        top.addView(titleBox, tlp);
        root.addView(top);

        statusPanel = new LinearLayout(this);
        statusPanel.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams statusLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        statusLp.setMargins(0, UiFactory.dp(this, 10), 0, UiFactory.dp(this, 12));
        root.addView(statusPanel, statusLp);
        allApps = loadApps();
        renderStatusPanel();

        GridLayout desktop = new GridLayout(this);
        desktop.setColumnCount(wide ? 6 : 4);
        desktop.setPadding(0, UiFactory.dp(this, 8), 0, UiFactory.dp(this, 8));
        addDesktopActionTile(desktop, "Chat", "ic_proto_chat", v -> startActivity(new Intent(this, com.enigma.familylinklite.chat.ChildChatActivity.class)));
        addDesktopActionTile(desktop, "Ask parent", "ic_proto_bell", v -> startActivity(new Intent(this, MainActivity.class).putExtra("open", "child_requests")));
        addDesktopTile(desktop, "Browser", findPreferredApp(new String[]{"com.android.chrome", "com.sec.android.app.sbrowser", "org.mozilla.firefox", "com.microsoft.emmx"}, "Browser"));
        addDesktopTile(desktop, "Play Store", findPreferredApp(new String[]{"com.android.vending"}, "Play Store"));
        for (String pkg : desktopPackages()) {
            AppItem item = findAppByPackage(pkg);
            if (item != null) addDesktopTile(desktop, item.label, item);
        }
        root.addView(desktop, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER);
        bar.setPadding(0, UiFactory.dp(this, 8), 0, 0);
        bar.addView(navButton("\u25A6", "Apps", v -> openDrawer()), new LinearLayout.LayoutParams(0, UiFactory.dp(this, 62), 1));
        bar.addView(navButton("\uD83D\uDCAC", "Chat", v -> startActivity(new Intent(this, com.enigma.familylinklite.chat.ChildChatActivity.class))), new LinearLayout.LayoutParams(0, UiFactory.dp(this, 62), 1));
        bar.addView(navButton("?", "Ask", v -> startActivity(new Intent(this, MainActivity.class).putExtra("open", "child_requests"))), new LinearLayout.LayoutParams(0, UiFactory.dp(this, 62), 1));
        bar.addView(navButton("\u22EE", "Menu", v -> startActivity(new Intent(this, MainActivity.class).putExtra("open", "child_dashboard"))), new LinearLayout.LayoutParams(0, UiFactory.dp(this, 62), 1));
        root.addView(bar);
    }

    private void openDrawer() {
        drawerOpen = true;
        render();
    }

    private void closeDrawer() {
        drawerOpen = false;
        render();
    }

    private TextView navButton(String icon, String label, View.OnClickListener click) {
        TextView view = UiFactory.text(this, icon + "\n" + label, 13);
        view.setGravity(Gravity.CENTER);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        view.setTextColor(UiFactory.textColor(this));
        view.setBackground(UiFactory.actionShape(this, UiFactory.panel(this), 12, UiFactory.border(this)));
        view.setPadding(0, UiFactory.dp(this, 6), 0, UiFactory.dp(this, 6));
        view.setOnClickListener(click);
        return view;
    }

    private void addDesktopTile(GridLayout desktop, String fallbackLabel, AppItem item) {
        LinearLayout tile = new LinearLayout(this);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER);
        tile.setPadding(UiFactory.dp(this, 6), UiFactory.dp(this, 6), UiFactory.dp(this, 6), UiFactory.dp(this, 6));
        ImageView icon = new ImageView(this);
        if (item != null) icon.setImageDrawable(item.icon);
        else icon.setImageResource(R.mipmap.ic_launcher);
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        icon.setBackground(UiFactory.actionShape(this, UiFactory.panel(this), 16, UiFactory.border(this)));
        icon.setPadding(UiFactory.dp(this, 10), UiFactory.dp(this, 10), UiFactory.dp(this, 10), UiFactory.dp(this, 10));
        tile.addView(icon, new LinearLayout.LayoutParams(UiFactory.dp(this, 64), UiFactory.dp(this, 64)));
        TextView label = UiFactory.text(this, item == null ? fallbackLabel : item.label, 12);
        label.setGravity(Gravity.CENTER);
        label.setMaxLines(2);
        label.setPadding(0, UiFactory.dp(this, 4), 0, 0);
        tile.addView(label, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tile.setOnClickListener(v -> {
            if (item != null) openApp(item);
            else Toast.makeText(this, fallbackLabel + " is not installed", Toast.LENGTH_SHORT).show();
        });
        tile.setOnLongClickListener(v -> {
            if (item != null && !isFixedDesktopApp(item.packageName)) removeDesktopApp(item.packageName);
            return true;
        });
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.width = UiFactory.dp(this, 96);
        lp.height = UiFactory.dp(this, 110);
        lp.setMargins(UiFactory.dp(this, 4), UiFactory.dp(this, 4), UiFactory.dp(this, 4), UiFactory.dp(this, 4));
        desktop.addView(tile, lp);
    }

    private void addDesktopActionTile(GridLayout desktop, String labelText, String drawable, View.OnClickListener click) {
        LinearLayout tile = new LinearLayout(this);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER);
        tile.setPadding(UiFactory.dp(this, 6), UiFactory.dp(this, 6), UiFactory.dp(this, 6), UiFactory.dp(this, 6));
        ImageView icon = new ImageView(this);
        int res = getResources().getIdentifier(drawable, "drawable", getPackageName());
        icon.setImageResource(res != 0 ? res : R.drawable.ic_proto_question);
        icon.setColorFilter(UiFactory.blue());
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        icon.setBackground(UiFactory.actionShape(this, UiFactory.panel(this), 16, UiFactory.border(this)));
        icon.setPadding(UiFactory.dp(this, 14), UiFactory.dp(this, 14), UiFactory.dp(this, 14), UiFactory.dp(this, 14));
        tile.addView(icon, new LinearLayout.LayoutParams(UiFactory.dp(this, 64), UiFactory.dp(this, 64)));
        TextView label = UiFactory.text(this, labelText, 12);
        label.setGravity(Gravity.CENTER);
        label.setMaxLines(2);
        label.setPadding(0, UiFactory.dp(this, 4), 0, 0);
        tile.addView(label, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tile.setOnClickListener(click);
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.width = UiFactory.dp(this, 96);
        lp.height = UiFactory.dp(this, 110);
        lp.setMargins(UiFactory.dp(this, 4), UiFactory.dp(this, 4), UiFactory.dp(this, 4), UiFactory.dp(this, 4));
        desktop.addView(tile, lp);
    }

    private boolean isFixedDesktopApp(String pkg) {
        return "com.android.vending".equals(pkg) || pkg.toLowerCase(Locale.US).contains("browser") || pkg.toLowerCase(Locale.US).contains("chrome");
    }

    private AppItem findPreferredApp(String[] packages, String labelContains) {
        for (String pkg : packages) {
            AppItem found = findAppByPackage(pkg);
            if (found != null) return found;
        }
        String needle = labelContains.toLowerCase(Locale.US);
        for (AppItem item : allApps) {
            String haystack = (item.label + " " + item.packageName).toLowerCase(Locale.US);
            if (haystack.contains(needle.toLowerCase(Locale.US))) return item;
        }
        return null;
    }

    private AppItem findAppByPackage(String pkg) {
        if (pkg == null || pkg.length() == 0) return null;
        for (AppItem item : allApps) {
            if (pkg.equals(item.packageName)) return item;
        }
        return null;
    }

    private ArrayList<String> desktopPackages() {
        ArrayList<String> out = new ArrayList<>();
        String raw = getSharedPreferences("launcher", 0).getString("desktopApps", "");
        for (String part : raw.split("\\|")) {
            String pkg = part.trim();
            if (pkg.length() > 0 && !out.contains(pkg) && !isFixedDesktopApp(pkg)) out.add(pkg);
        }
        return out;
    }

    private void addDesktopApp(String pkg) {
        ArrayList<String> apps = desktopPackages();
        if (!apps.contains(pkg)) apps.add(pkg);
        saveDesktopApps(apps);
        Toast.makeText(this, "Added to desktop", Toast.LENGTH_SHORT).show();
    }

    private void removeDesktopApp(String pkg) {
        ArrayList<String> apps = desktopPackages();
        apps.remove(pkg);
        saveDesktopApps(apps);
        Toast.makeText(this, "Removed from desktop", Toast.LENGTH_SHORT).show();
        render();
    }

    private void saveDesktopApps(ArrayList<String> apps) {
        StringBuilder sb = new StringBuilder();
        for (String pkg : apps) {
            if (sb.length() > 0) sb.append("|");
            sb.append(pkg);
        }
        getSharedPreferences("launcher", 0).edit().putString("desktopApps", sb.toString()).apply();
    }

    private boolean drawerSwipeEnabled() {
        return getSharedPreferences("launcher", 0).getBoolean("drawerSwipe", true);
    }

    private void renderApps() {
        allApps = loadApps();
        renderFolders();
        renderStatusPanel();
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
        sortApps(apps);
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
        ArrayList<String> folders = launcherFolders();
        for (String folder : folders) {
            int count = "All".equals(folder) ? allApps.size() : countFolder(folder);
            if (!"All".equals(folder) && count == 0 && !isCustomFolder(folder)) continue;
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
        TextView add = UiFactory.text(this, "+ Folder", 13);
        add.setGravity(Gravity.CENTER);
        add.setTypeface(Typeface.DEFAULT_BOLD);
        add.setSingleLine(true);
        add.setTextColor(UiFactory.blue());
        add.setBackground(UiFactory.actionShape(this, UiFactory.panel(this), 999, UiFactory.border(this)));
        add.setPadding(UiFactory.dp(this, 12), 0, UiFactory.dp(this, 12), 0);
        LinearLayout.LayoutParams alp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, UiFactory.dp(this, 36));
        alp.setMargins(0, 0, UiFactory.dp(this, 8), 0);
        folderStrip.addView(add, alp);
        add.setOnClickListener(v -> askCreateFolder(null));
    }

    private ArrayList<String> launcherFolders() {
        ArrayList<String> folders = new ArrayList<>();
        String[] defaults = new String[]{"All", "Games", "Education", "Video", "Social", "Browser", "Tools", "Other"};
        for (String folder : defaults) folders.add(folder);
        String raw = getSharedPreferences("launcher", 0).getString("customFolders", "");
        for (String part : raw.split("\\|")) {
            String clean = part.trim();
            if (clean.length() > 0 && !folders.contains(clean)) folders.add(clean);
        }
        return folders;
    }

    private boolean isCustomFolder(String folder) {
        String raw = getSharedPreferences("launcher", 0).getString("customFolders", "");
        for (String part : raw.split("\\|")) {
            if (folder.equals(part.trim())) return true;
        }
        return false;
    }

    private String assignedFolder(String pkg) {
        return getSharedPreferences("launcher", 0).getString("folder_" + pkg, "").trim();
    }

    private void saveAssignedFolder(String pkg, String folder) {
        getSharedPreferences("launcher", 0).edit().putString("folder_" + pkg, folder == null ? "" : folder.trim()).apply();
    }

    private void askCreateFolder(AppItem moveAfterCreate) {
        EditText input = UiFactory.oneLine(this, "Folder name");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        new AlertDialog.Builder(this)
                .setTitle("New folder")
                .setView(input)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Create", (d, w) -> {
                    String name = input.getText().toString().trim();
                    if (name.length() == 0) {
                        Toast.makeText(this, "Write a folder name first", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    rememberCustomFolder(name);
                    if (moveAfterCreate != null) saveAssignedFolder(moveAfterCreate.packageName, name);
                    selectedFolder = name;
                    renderApps();
                })
                .show();
    }

    private void rememberCustomFolder(String name) {
        ArrayList<String> folders = launcherFolders();
        if (folders.contains(name)) return;
        String raw = getSharedPreferences("launcher", 0).getString("customFolders", "");
        String next = raw.trim().length() == 0 ? name : raw + "|" + name;
        getSharedPreferences("launcher", 0).edit().putString("customFolders", next).apply();
    }

    private void showAppFolderMenu(AppItem item) {
        ArrayList<String> folders = launcherFolders();
        ArrayList<String> options = new ArrayList<>();
        options.add("Add to desktop");
        for (String folder : folders) {
            if (!"All".equals(folder)) options.add("Move to " + folder);
        }
        options.add("Create new folder");
        options.add("Clear folder");
        String[] labels = options.toArray(new String[0]);
        new AlertDialog.Builder(this)
                .setTitle(item.label)
                .setItems(labels, (dialog, which) -> {
                    String picked = labels[which];
                    if ("Add to desktop".equals(picked)) {
                        addDesktopApp(item.packageName);
                        closeDrawer();
                        return;
                    }
                    if ("Create new folder".equals(picked)) {
                        askCreateFolder(item);
                        return;
                    }
                    if ("Clear folder".equals(picked)) {
                        saveAssignedFolder(item.packageName, "");
                        selectedFolder = "All";
                        renderApps();
                        return;
                    }
                    String folder = picked.replaceFirst("^Move to ", "").trim();
                    rememberCustomFolder(folder);
                    saveAssignedFolder(item.packageName, folder);
                    selectedFolder = folder;
                    renderApps();
                })
                .show();
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
        Map<String, UsageInfo> usage = usageToday();
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
            UsageInfo info = usage.containsKey(pkg) ? usage.get(pkg) : new UsageInfo(0, 0);
            String folder = assignedFolder(pkg);
            if (folder.length() == 0) folder = categoryFor(ri.activityInfo.applicationInfo, label, pkg);
            apps.add(new AppItem(label, pkg, icon, launch, folder, info.todayMs, info.lastUsed));
        }
        sortApps(apps);
        return apps;
    }

    private void sortApps(ArrayList<AppItem> apps) {
        if ("recent".equals(sortMode)) {
            apps.sort((a, b) -> {
                int recent = Long.compare(b.lastUsed, a.lastUsed);
                return recent != 0 ? recent : a.label.compareToIgnoreCase(b.label);
            });
        } else {
            apps.sort((a, b) -> a.label.compareToIgnoreCase(b.label));
        }
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
        if (system && !updatedSystem && !isAllowedFixedSystemApp(lower)) return false;
        return packageManager.getLaunchIntentForPackage(pkg) != null;
    }

    private boolean isAllowedFixedSystemApp(String pkg) {
        return "com.android.vending".equals(pkg)
                || pkg.contains("chrome")
                || pkg.contains("browser")
                || pkg.contains("sbrowser")
                || pkg.contains("firefox")
                || pkg.contains("emmx");
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
        row.setPadding(UiFactory.dp(this, 12), compactRows ? UiFactory.dp(this, 6) : UiFactory.dp(this, 10), UiFactory.dp(this, 12), compactRows ? UiFactory.dp(this, 6) : UiFactory.dp(this, 10));
        row.setMinimumHeight(UiFactory.dp(this, compactRows ? 58 : 76));
        row.setBackground(UiFactory.actionShape(this, UiFactory.panel(this), 14, UiFactory.border(this)));

        ImageView icon = new ImageView(this);
        icon.setImageDrawable(item.icon);
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        icon.setBackground(UiFactory.actionShape(this, UiFactory.panel2(this), 14, UiFactory.border(this)));
        icon.setPadding(UiFactory.dp(this, 8), UiFactory.dp(this, 8), UiFactory.dp(this, 8), UiFactory.dp(this, 8));
        int iconSize = compactRows ? 44 : 56;
        row.addView(icon, new LinearLayout.LayoutParams(UiFactory.dp(this, iconSize), UiFactory.dp(this, iconSize)));

        LinearLayout textBox = new LinearLayout(this);
        textBox.setOrientation(LinearLayout.VERTICAL);
        TextView name = UiFactory.text(this, item.label, 17);
        name.setTypeface(Typeface.DEFAULT_BOLD);
        name.setSingleLine(true);
        name.setPadding(0, 0, 0, 0);
        boolean blocked = blockedUntil(item.packageName) > System.currentTimeMillis();
        TextView folder = UiFactory.mutedText(this, blocked ? "Blocked by parent" : launcherMeta(item), compactRows ? 11 : 12);
        if (blocked) folder.setTextColor(UiFactory.red());
        folder.setPadding(0, UiFactory.dp(this, 2), 0, 0);
        textBox.addView(name);
        textBox.addView(folder);
        LinearLayout.LayoutParams textLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        textLp.setMargins(UiFactory.dp(this, 14), 0, UiFactory.dp(this, 8), 0);
        row.addView(textBox, textLp);

        TextView open = UiFactory.text(this, blocked ? "\uD83D\uDD12" : "\u203A", blocked ? 20 : 26);
        open.setGravity(Gravity.CENTER);
        open.setTextColor(blocked ? UiFactory.red() : UiFactory.blue());
        open.setPadding(0, 0, 0, 0);
        row.addView(open, new LinearLayout.LayoutParams(UiFactory.dp(this, 30), UiFactory.dp(this, 44)));

        row.setOnClickListener(v -> openApp(item));
        row.setOnLongClickListener(v -> {
            showAppFolderMenu(item);
            return true;
        });
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
        row.setOnClickListener(v -> openShortcut(data.action));
        return row;
    }

    private TextView controlChip(String label) {
        TextView chip = UiFactory.text(this, label, 13);
        chip.setGravity(Gravity.CENTER);
        chip.setTypeface(Typeface.DEFAULT_BOLD);
        chip.setSingleLine(true);
        chip.setBackground(UiFactory.actionShape(this, UiFactory.panel(this), 999, UiFactory.border(this)));
        return chip;
    }

    private void renderStatusPanel() {
        if (statusPanel == null) return;
        statusPanel.removeAllViews();
        long now = System.currentTimeMillis();
        SharedPreferences rules = getSharedPreferences("rules", 0);
        long lockUntil = rules.getLong("lock_until", 0);
        String mode = rules.getString("lock_mode", "");
        String profile = rules.getString("active_profile", "Free use");
        boolean limited = lockUntil > now;

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(UiFactory.dp(this, 10), UiFactory.dp(this, 8), UiFactory.dp(this, 10), UiFactory.dp(this, 8));
        card.setBackground(UiFactory.actionShape(this, limited ? Color.rgb(255, 238, 241) : UiFactory.panel(this), 12, limited ? UiFactory.red() : UiFactory.border(this)));

        TextView title = UiFactory.text(this, limited ? "Limited time" : "Supervised use", 14);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setPadding(0, 0, 0, 0);
        title.setTextColor(limited ? UiFactory.red() : UiFactory.textColor(this));
        card.addView(title);

        TextView timer = UiFactory.text(this, limited ? restrictionText(mode, lockUntil) : "No active restriction", limited ? 22 : 15);
        timer.setTypeface(Typeface.DEFAULT_BOLD);
        timer.setPadding(0, UiFactory.dp(this, 2), 0, 0);
        timer.setTextColor(limited ? UiFactory.red() : UiFactory.mutedTextColor(this));
        card.addView(timer);

        TextView profileView = UiFactory.mutedText(this, "Profile: " + profile, 12);
        profileView.setPadding(0, UiFactory.dp(this, 2), 0, 0);
        card.addView(profileView);

        LinearLayout lockedIcons = new LinearLayout(this);
        lockedIcons.setOrientation(LinearLayout.HORIZONTAL);
        lockedIcons.setPadding(0, UiFactory.dp(this, 6), 0, 0);
        int shown = 0;
        for (AppItem item : allApps) {
            if (blockedUntil(item.packageName) <= now) continue;
            ImageView icon = new ImageView(this);
            icon.setImageDrawable(item.icon);
            icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            icon.setBackground(UiFactory.actionShape(this, UiFactory.panel(this), 10, UiFactory.border(this)));
            icon.setPadding(UiFactory.dp(this, 5), UiFactory.dp(this, 5), UiFactory.dp(this, 5), UiFactory.dp(this, 5));
            LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(UiFactory.dp(this, 34), UiFactory.dp(this, 34));
            ilp.setMargins(0, 0, UiFactory.dp(this, 6), 0);
            lockedIcons.addView(icon, ilp);
            if (++shown >= 5) break;
        }
        if (shown > 0) card.addView(lockedIcons);
        statusPanel.addView(card);
    }

    private String restrictionText(String mode, long until) {
        long seconds = Math.max(1, (until - System.currentTimeMillis() + 999) / 1000);
        long minutes = seconds / 60;
        long sec = seconds % 60;
        String prefix = "timeout".equals(mode) ? "Timeout" : "Blocked";
        if (minutes >= 60) return prefix + " " + (minutes / 60) + "h " + (minutes % 60) + "m";
        return prefix + " " + minutes + ":" + String.format(Locale.US, "%02d", sec);
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
            long blocked = blockedUntil(item.packageName);
            if (blocked > System.currentTimeMillis()) {
                Intent attention = new Intent(this, AttentionActivity.class);
                attention.putExtra("title", "App blocked");
                attention.putExtra("text", item.label + " is blocked by your parent.");
                attention.putExtra("blocking", true);
                attention.putExtra("reason", "blocked_" + item.packageName);
                startActivity(attention);
                return;
            }
            Intent launch = new Intent(item.launch);
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launch);
        } catch (Exception e) {
            Toast.makeText(this, "Could not open " + item.label, Toast.LENGTH_SHORT).show();
        }
    }
    private long blockedUntil(String pkg) {
        return getSharedPreferences("rules", 0).getLong("blocked_until_" + pkg, 0);
    }
    private void openShortcut(String action) {
        Intent intent;
        if ("chat".equals(action)) intent = new Intent(this, com.enigma.familylinklite.chat.ChildChatActivity.class);
        else if ("settings".equals(action)) intent = new Intent(this, MainActivity.class).putExtra("open", "child_dashboard");
        else intent = new Intent(this, MainActivity.class).putExtra("open", "child_dashboard");
        startActivity(intent);
    }
    private void tryStartLockTaskIfAllowed() {
        try {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
            if (dpm != null && dpm.isAdminActive(new ComponentName(this, AdminReceiver.class)) && dpm.isLockTaskPermitted(getPackageName())) {
                startLockTask();
            }
        } catch (Exception ignored) {}
    }

    private String launcherMeta(AppItem item) {
        String time = usageText(item.todayMs);
        if (time.length() == 0) return item.category;
        return item.category + " - today " + time;
    }
    private String usageText(long ms) {
        long min = ms / 60000L;
        if (min <= 0) return "";
        long h = min / 60L;
        long m = min % 60L;
        if (h > 0 && m > 0) return h + "h " + m + "m";
        if (h > 0) return h + "h";
        return m + "m";
    }
    private Map<String, UsageInfo> usageToday() {
        Map<String, UsageInfo> out = new HashMap<>();
        try {
            UsageStatsManager manager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
            if (manager == null) return out;
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            List<UsageStats> stats = manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, c.getTimeInMillis(), System.currentTimeMillis());
            if (stats == null) return out;
            for (UsageStats st : stats) {
                if (st == null || st.getPackageName() == null) continue;
                long used = st.getTotalTimeInForeground();
                long last = st.getLastTimeUsed();
                if (used <= 0 && last <= 0) continue;
                out.put(st.getPackageName(), new UsageInfo(used, last));
            }
        } catch (Exception ignored) {}
        return out;
    }

    private static final class AppItem {
        final String label;
        final String packageName;
        final Drawable icon;
        final Intent launch;
        final String category;
        final long todayMs;
        final long lastUsed;

        AppItem(String label, String packageName, Drawable icon, Intent launch, String category, long todayMs, long lastUsed) {
            this.label = label;
            this.packageName = packageName;
            this.icon = icon;
            this.launch = launch;
            this.category = category;
            this.todayMs = todayMs;
            this.lastUsed = lastUsed;
        }
    }

    private static final class UsageInfo {
        final long todayMs;
        final long lastUsed;

        UsageInfo(long todayMs, long lastUsed) {
            this.todayMs = todayMs;
            this.lastUsed = lastUsed;
        }
    }

    private static final class ButtonLike {
        final String label;
        final String drawable;
        final String action;

        ButtonLike(String label, String drawable, String action) {
            this.label = label;
            this.drawable = drawable;
            this.action = action;
        }
    }
}
