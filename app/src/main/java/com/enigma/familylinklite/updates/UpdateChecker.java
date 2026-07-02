package com.enigma.familylinklite.updates;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.enigma.familylinklite.core.AppConfig;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    public static void checkLatestRelease(Context context, String repo, TextView status) {
        if (status != null) status.setText("Checking GitHub Releases...");
        Context appContext = context.getApplicationContext();
        Handler main = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            try {
                URL url = new URL("https://api.github.com/repos/" + repo + "/releases/latest");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(8000);
                connection.setRequestProperty("Accept", "application/vnd.github+json");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) body.append(line);

                JSONObject json = new JSONObject(body.toString());
                String tag = json.optString("tag_name", "");
                String htmlUrl = json.optString("html_url", "");
                String normalized = tag.replaceFirst("^[vV]", "");
                boolean updateAvailable = VersionUtils.compareVersions(normalized, AppConfig.APP_VERSION) > 0;

                SharedPreferences preferences = appContext.getSharedPreferences("p", 0);
                preferences.edit()
                        .putString("latestVersion", tag.length() > 0 ? tag : normalized)
                        .putString("latestReleaseUrl", htmlUrl)
                        .putBoolean("updateAvailable", updateAvailable)
                        .apply();

                main.post(() -> {
                    if (status != null) {
                        status.setText(updateAvailable ? "Update available: " + tag : "Up to date: " + AppConfig.APP_VERSION);
                    }
                });
            } catch (Exception e) {
                main.post(() -> {
                    if (status != null) status.setText("Update check failed: " + e.getMessage());
                });
            }
        }).start();
    }
}
