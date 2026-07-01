package com.enigma.familylinklite.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.enigma.familylinklite.core.AppConfig;
import com.enigma.familylinklite.protocol.CommandPacket;
import com.enigma.familylinklite.storage.SavedConnection;
import com.enigma.familylinklite.storage.CommandStatusStore;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONObject;

public final class CommandClient {
    public interface Callback { void call(String message); }

    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public CommandClient(Context context) {
        this.context = context.getApplicationContext();
    }

    public void send(String childIp, byte[] key, String type, String value, Callback ok, Callback err) {
        new Thread(() -> {
            boolean connected = false;
            Socket socket = null;
            try {
                if (childIp == null || childIp.length() == 0 || key == null) {
                    context.getSharedPreferences(AppConfig.PREFS, 0).edit().putBoolean("pairingMismatch", false).apply();
                    mainHandler.post(() -> err.call("No child connection saved"));
                    return;
                }
                SavedConnection connection = new SavedConnection(context);
                JSONObject packet = CommandPacket.create(type, value, connection.parentName(), connection.localDeviceId());
                String commandId = packet.optString("id", "");
                String commandLabel = type == null ? "command" : type;
                CommandStatusStore.record(context, commandId, commandLabel, "Sending", "", value);
                String plain = packet.toString();
                socket = new Socket();
                socket.connect(new InetSocketAddress(childIp, AppConfig.COMMAND_PORT), 2500);
                connected = true;
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println(encrypt(plain, key));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String encryptedResponse = reader.readLine();
                if (encryptedResponse == null || encryptedResponse.trim().length() == 0) {
                    throw new SecurityException("empty handshake response");
                }
                String response = decrypt(encryptedResponse, key);
                try { socket.close(); } catch (Exception ignored) {}
                context.getSharedPreferences(AppConfig.PREFS, 0).edit()
                        .putBoolean("pairingMismatch", false)
                        .remove("pairingMismatchMessage")
                        .apply();
                CommandStatusStore.record(context, commandId, commandLabel, "Delivered", "Child responded", value);
                String lower = response == null ? "" : response.toLowerCase(java.util.Locale.US);
                if (lower.contains("unknown command") || lower.contains("invalid") || lower.contains("failed") || lower.contains("unavailable")) {
                    CommandStatusStore.record(context, commandId, commandLabel, "Failed", response, value);
                } else {
                    CommandStatusStore.record(context, commandId, commandLabel, "Executed", response, value);
                }
                mainHandler.post(() -> ok.call(response));
            } catch (Exception e) {
                try { if (socket != null) socket.close(); } catch (Exception ignored) {}
                if (connected) {
                    String message = "Possible pairing mismatch: a server answered at the saved child IP, but it did not complete the Parental-Link handshake. The child app may have been reset, the IP may now belong to another device, or the saved pairing data may be stale.";
                    context.getSharedPreferences(AppConfig.PREFS, 0).edit()
                            .putBoolean("pairingMismatch", true)
                            .putString("pairingMismatchMessage", message)
                            .putLong("pairingMismatchAt", System.currentTimeMillis())
                            .apply();
                    CommandStatusStore.record(context, "", type == null ? "command" : type, "Failed", message, value);
                    mainHandler.post(() -> err.call(message));
                } else {
                    context.getSharedPreferences(AppConfig.PREFS, 0).edit().putBoolean("pairingMismatch", false).apply();
                    mainHandler.post(() -> err.call("Command failed: " + e.getMessage() + "\nCheck that the tablet app is in Child mode, the child server notification is visible, and both devices are on the same Wi-Fi."));
                }
            }
        }).start();
    }

    public static String encrypt(String plain, byte[] key) throws Exception {
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(128, iv));
        byte[] enc = c.doFinal(plain.getBytes(StandardCharsets.UTF_8));
        byte[] all = new byte[iv.length + enc.length];
        System.arraycopy(iv, 0, all, 0, iv.length);
        System.arraycopy(enc, 0, all, iv.length, enc.length);
        return Base64.getEncoder().encodeToString(all);
    }

    public static String decrypt(String data, byte[] key) throws Exception {
        byte[] all = Base64.getDecoder().decode(data);
        byte[] iv = Arrays.copyOfRange(all, 0, 12);
        byte[] enc = Arrays.copyOfRange(all, 12, all.length);
        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(128, iv));
        return new String(c.doFinal(enc), StandardCharsets.UTF_8);
    }
}
