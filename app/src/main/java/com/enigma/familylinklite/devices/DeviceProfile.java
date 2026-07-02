package com.enigma.familylinklite.devices;

import android.content.Context;
import android.content.SharedPreferences;

public class DeviceProfile {
    private final SharedPreferences preferences;

    public DeviceProfile(Context context) {
        preferences = context.getSharedPreferences("p", 0);
    }

    public String childNickname() {
        return preferences.getString("childNickname", "Child tablet");
    }

    public String childIcon() {
        return preferences.getString("childIcon", "📟");
    }

    public String childIp() {
        return preferences.getString("childIp", "Not connected");
    }

    public void saveChildDetails(String nickname, String icon) {
        preferences.edit().putString("childNickname", nickname).putString("childIcon", icon).apply();
    }

    public String localDeviceId() {
        return preferences.getString("localDeviceId", "");
    }

    public String parentName() {
        return preferences.getString("parentName", "Parent Control");
    }
}
