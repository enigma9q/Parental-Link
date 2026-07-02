package com.enigma.familylinklite.audio;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.enigma.familylinklite.ui.UiFactory;
import java.io.File;

public class ChildAudioMessageActivity extends Activity {
    MediaRecorder recorder;
    File output;
    TextView status;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        if (android.os.Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 44);
        }
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(36, 36, 36, 36);
        TextView title = UiFactory.text(this, "Send audio to parent", 24);
        title.setGravity(Gravity.CENTER);
        root.addView(title);
        status = UiFactory.text(this, "Record a short audio message. Sending to the parent is a local placeholder until the secure audio channel is connected.", 15);
        status.setGravity(Gravity.CENTER);
        root.addView(status);
        Button record = UiFactory.button(this, "Record");
        Button stop = UiFactory.button(this, "Stop");
        Button send = UiFactory.button(this, "Send to parent");
        Button close = UiFactory.button(this, "Close");
        root.addView(record);
        root.addView(stop);
        root.addView(send);
        root.addView(close);
        record.setOnClickListener(v -> startRecording());
        stop.setOnClickListener(v -> stopRecording());
        send.setOnClickListener(v -> {
            if (output != null && output.exists()) {
                String stamp = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.US).format(new java.util.Date());
                getSharedPreferences("p", 0).edit().putString("lastChildAudioMessage", stamp).apply();
                status.setText("Audio message marked ready for parent at " + stamp + ".");
            } else {
                status.setText("Record an audio message first.");
            }
        });
        close.setOnClickListener(v -> finish());
        setContentView(root);
    }

    void startRecording() {
        try {
            output = new File(getCacheDir(), "child_audio_message.m4a");
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setOutputFile(output.getAbsolutePath());
            recorder.prepare();
            recorder.start();
            status.setText("Recording...");
        } catch (Exception e) {
            status.setText("Recording failed: " + e.getMessage());
        }
    }

    void stopRecording() {
        try {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
                recorder = null;
            }
            status.setText("Recording saved locally. Press Send to parent.");
        } catch (Exception e) {
            status.setText("Stop failed: " + e.getMessage());
        }
    }

    protected void onDestroy() {
        try { if (recorder != null) { recorder.release(); recorder = null; } } catch (Exception ignored) {}
        super.onDestroy();
    }
}
