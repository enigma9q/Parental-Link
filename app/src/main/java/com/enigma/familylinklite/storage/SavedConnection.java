package com.enigma.familylinklite.storage;

import android.content.Context;
import android.content.SharedPreferences;
import com.enigma.familylinklite.core.AppConfig;
import java.util.Base64;

public final class SavedConnection {
    private final SharedPreferences prefs;

    public SavedConnection(Context context) {
        this.prefs = context.getSharedPreferences(AppConfig.PREFS, 0);
    }

    public String role() { return prefs.getString("role", ""); }
    public String childIp() { return prefs.getString("childIp", ""); }
    public String parentKeyBase64() { return prefs.getString("parentKey", ""); }
    public String parentName() { return prefs.getString("parentName", AppConfig.DEFAULT_PARENT_NAME); }
    public String localDeviceId() { return prefs.getString("localDeviceId", ""); }

    public boolean hasParentConnection() {
        return "parent".equals(role()) && childIp().length() > 0 && parentKeyBase64().length() > 0;
    }

    public byte[] parentKeyBytesOrNull() {
        try {
            String saved = parentKeyBase64();
            return saved.length() == 0 ? null : Base64.getDecoder().decode(saved);
        } catch (Exception ignored) {
            return null;
        }
    }

    public void setRole(String role) {
        prefs.edit().putString("role", role).apply();
    }

    public void saveParentConnection(String ip, String parentKeyBase64) {
        prefs.edit()
                .putString("role", "parent")
                .putString("childIp", ip)
                .putString("parentKey", parentKeyBase64)
                .apply();
    }

    public void saveChildIp(String ip) {
        prefs.edit().putString("childIp", ip).apply();
    }
}
