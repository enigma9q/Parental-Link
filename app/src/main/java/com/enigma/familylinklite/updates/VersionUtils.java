package com.enigma.familylinklite.updates;

public final class VersionUtils {
    private VersionUtils() {}

    public static int compareVersions(String a, String b) {
        try {
            String[] aa = a.split("\\.");
            String[] bb = b.split("\\.");
            for (int i = 0; i < Math.max(aa.length, bb.length); i++) {
                int x = i < aa.length ? Integer.parseInt(aa[i].replaceAll("\\D.*$", "")) : 0;
                int y = i < bb.length ? Integer.parseInt(bb[i].replaceAll("\\D.*$", "")) : 0;
                if (x != y) return x - y;
            }
        } catch (Exception ignored) {}
        return a.compareTo(b);
    }
}
