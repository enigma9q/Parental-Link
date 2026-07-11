package com.enigma.familylinklite.storage;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class PermissionReport {
    public final Map<String, Boolean> values = new LinkedHashMap<>();

    public static PermissionReport parse(String raw) {
        PermissionReport r = new PermissionReport();
        r.putDefaults();
        if (raw == null) return r;
        String[] parts = raw.split(",");
        for (String part : parts) {
            int idx = part.indexOf('=');
            if (idx <= 0) continue;
            String key = part.substring(0, idx).trim();
            String value = part.substring(idx + 1).trim().toLowerCase(Locale.US);
            if (r.values.containsKey(key)) r.values.put(key, "true".equals(value));
        }
        return r;
    }

    private void putDefaults() {
        values.put("admin", false);
        values.put("usage", false);
        values.put("accessibility", false);
        values.put("notifications", false);
        values.put("overlay", false);
        values.put("dndPolicy", false);
        values.put("battery", false);
    }

    public boolean hasData(String raw) {
        return raw != null && raw.trim().length() > 0;
    }

    public boolean ok() {
        for (Boolean v : values.values()) if (!Boolean.TRUE.equals(v)) return false;
        return true;
    }

    public String summary(String raw) {
        if (!hasData(raw)) return "Permissions: unknown";
        return ok() ? "Permissions OK" : "Permissions needed";
    }

    public String details(String raw) {
        if (!hasData(raw)) return "No child permission report received yet.";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Boolean> e : values.entrySet()) {
            if (sb.length() > 0) sb.append('\n');
            sb.append(label(e.getKey())).append(": ").append(e.getValue() ? "OK" : "Needed");
        }
        return sb.toString();
    }

    public String missingCompact() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Boolean> e : values.entrySet()) {
            if (!Boolean.TRUE.equals(e.getValue())) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(label(e.getKey()));
            }
        }
        return sb.toString();
    }

    private String label(String key) {
        if ("admin".equals(key)) return "Device Admin";
        if ("usage".equals(key)) return "Usage Access";
        if ("accessibility".equals(key)) return "Accessibility";
        if ("notifications".equals(key)) return "Notifications";
        if ("overlay".equals(key)) return "Overlay";
        if ("dndPolicy".equals(key)) return "DND Access";
        if ("battery".equals(key)) return "Battery unrestricted";
        return key;
    }
}
