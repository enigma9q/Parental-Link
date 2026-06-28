package com.enigma.familylinklite.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.enigma.familylinklite.core.AppConfig;
import com.enigma.familylinklite.protocol.CommandPacket;
import com.enigma.familylinklite.storage.SavedConnection;
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

public final class CommandClient {
    public interface Callback { void call(String message); }

    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public CommandClient(Context context) {
        this.context = context.getApplicationContext();
    }

    public void send(String childIp, byte[] key, String type, String value, Callback ok, Callback err) {
        new Thread(() -> {
            try {
                if (childIp == null || childIp.length() == 0 || key == null) {
                    mainHandler.post(() -> err.call("No child connection saved"));
                    return;
                }
                SavedConnection connection = new SavedConnection(context);
                String plain = CommandPacket.create(type, value, connection.parentName(), connection.localDeviceId()).toString();
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(childIp, AppConfig.COMMAND_PORT), 2500);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println(encrypt(plain, key));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = decrypt(reader.readLine(), key);
                socket.close();
                mainHandler.post(() -> ok.call(response));
            } catch (Exception e) {
                mainHandler.post(() -> err.call("Command failed: " + e.getMessage() + "\nCheck that the tablet app is in Child mode, the child server notification is visible, and both devices are on the same Wi-Fi."));
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
