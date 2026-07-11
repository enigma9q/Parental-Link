package com.enigma.familylinklite.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public final class BlockedAppEntry {
    public final String label;
    public final String pkg;
    public final String untilText;

    public BlockedAppEntry(String label, String pkg, String untilText) {
        this.label = label == null ? "" : label.trim();
        this.pkg = pkg == null ? "" : pkg.trim();
        this.untilText = untilText == null ? "" : untilText.trim();
    }

    public String displayName() {
        return label.length() > 0 ? label : pkg;
    }

    public static ArrayList<BlockedAppEntry> parseList(String raw) {
        ArrayList<BlockedAppEntry> out = new ArrayList<>();
        if (raw == null) return out;
        for (String line : raw.split("\\n")) {
            String l = line == null ? "" : line.trim();
            if (l.length() == 0) continue;
            String low = l.toLowerCase();
            if (low.startsWith("blocked apps") || low.contains("no blocked")) continue;
            String[] p = l.split("\\|", 3);
            if (p.length >= 2) out.add(new BlockedAppEntry(p[0], p[1], p.length >= 3 ? p[2] : ""));
            else out.add(new BlockedAppEntry(l, l, ""));
        }
        sortByName(out);
        return out;
    }

    public static ArrayList<BlockedAppEntry> parseInstalledList(String raw) {
        ArrayList<BlockedAppEntry> out = new ArrayList<>();
        if (raw == null) return out;
        for (String line : raw.split("\\n")) {
            String l = line == null ? "" : line.trim();
            if (l.length() == 0) continue;
            String low = l.toLowerCase();
            if (low.startsWith("apps") || low.startsWith("error")) continue;
            String[] p = l.split("\\|", 2);
            if (p.length >= 2) out.add(new BlockedAppEntry(p[0], p[1], ""));
        }
        sortByName(out);
        return out;
    }

    private static void sortByName(ArrayList<BlockedAppEntry> apps) {
        Collections.sort(apps, new Comparator<BlockedAppEntry>() {
            public int compare(BlockedAppEntry a, BlockedAppEntry b) {
                return a.displayName().compareToIgnoreCase(b.displayName());
            }
        });
    }
}
