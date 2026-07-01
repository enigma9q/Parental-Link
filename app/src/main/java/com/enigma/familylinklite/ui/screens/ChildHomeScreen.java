package com.enigma.familylinklite.ui.screens;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.enigma.familylinklite.ui.UiFactory;
import com.enigma.familylinklite.ui.LanguageManager;

public final class ChildHomeScreen {
    private ChildHomeScreen() {}

    public static final class PairViews {
        public TextView code;
        public TextView countdown;
        public ImageView qr;
    }

    public static PairViews render(
            Activity activity,
            LinearLayout root,
            boolean connected,
            boolean showPairingDetails,
            String parentName,
            boolean permissionsOk,
            View.OnClickListener restartServer,
            View.OnClickListener unlockSettings,
            View.OnClickListener showHidePairing,
            View.OnClickListener sendAudio,
            View.OnClickListener callParent,
            boolean chatUnread
    ) {
        PairViews pairViews = new PairViews();

        TextView title = UiFactory.text(activity, "Parental-Link", 26);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        LinearLayout statusCard = card(activity);
        TextView status = UiFactory.text(activity, connected ? "🟢 "+activity.getString(com.enigma.familylinklite.R.string.connected) : "⚫ "+activity.getString(com.enigma.familylinklite.R.string.waiting_for_parent), 20);
        status.setGravity(Gravity.CENTER);
        statusCard.addView(status);
        TextView parent = UiFactory.text(activity, connected ? "Parent: " + parentName : "Pair this device with a parent phone.", 16);
        parent.setGravity(Gravity.CENTER);
        statusCard.addView(parent);
        root.addView(statusCard);

        if (!connected || showPairingDetails) {
            LinearLayout pairing = card(activity);
            TextView pairTitle = UiFactory.text(activity, connected ? "Pairing details" : "Pair this tablet", 18);
            pairTitle.setGravity(Gravity.CENTER);
            pairing.addView(pairTitle);
            pairViews.code = UiFactory.text(activity, "--- ---", 36);
            pairViews.code.setGravity(Gravity.CENTER);
            pairing.addView(pairViews.code);
            pairViews.countdown = UiFactory.text(activity, "New code in --:--", 15);
            pairViews.countdown.setGravity(Gravity.CENTER);
            pairing.addView(pairViews.countdown);
            pairViews.qr = new ImageView(activity);
            pairViews.qr.setAdjustViewBounds(true);
            LinearLayout.LayoutParams qlp = new LinearLayout.LayoutParams(240, 240);
            qlp.gravity = Gravity.CENTER_HORIZONTAL;
            pairing.addView(pairViews.qr, qlp);
            TextView info = UiFactory.text(activity, connected ? "Pairing details are normally hidden after setup." : "Show this code or QR to a parent phone.", 14);
            info.setGravity(Gravity.CENTER);
            pairing.addView(info);
            root.addView(pairing);
        }

        if (connected) {
            Button togglePairing = UiFactory.button(activity, showPairingDetails ? activity.getString(com.enigma.familylinklite.R.string.hide_pairing_details) : activity.getString(com.enigma.familylinklite.R.string.show_pairing_details));
            root.addView(togglePairing);
            togglePairing.setOnClickListener(showHidePairing);
        }

        LinearLayout actions = card(activity);
        actions.addView(UiFactory.text(activity, "Actions", 18));
        Button chat = UiFactory.button(activity, chatUnread ? activity.getString(com.enigma.familylinklite.R.string.chat)+" • new" : activity.getString(com.enigma.familylinklite.R.string.chat));
        Button audio = UiFactory.button(activity, "Send audio");
        Button unlock = UiFactory.button(activity, activity.getString(com.enigma.familylinklite.R.string.unlock_settings));
        Button restart = UiFactory.button(activity, activity.getString(com.enigma.familylinklite.R.string.restart_server));
        actions.addView(chat);
        actions.addView(audio);
        actions.addView(unlock);
        actions.addView(restart);
        chat.setOnClickListener(callParent);
        audio.setOnClickListener(sendAudio);
        unlock.setOnClickListener(unlockSettings);
        restart.setOnClickListener(restartServer);
        root.addView(actions);

        LinearLayout permissionCard = card(activity);
        permissionCard.addView(UiFactory.text(activity, "Required access", 18));
        permissionCard.addView(UiFactory.text(activity, permissionsOk ? "OK" : "New permissions needed. Unlock settings to review.", 14));
        root.addView(permissionCard);

        LinearLayout backlog = card(activity);
        backlog.addView(UiFactory.text(activity, "Coming controls", 18));
        backlog.addView(UiFactory.text(activity, "• Parent call screen\n• Audio messages\n• Ask parent to unlock\n• Travel mode / remove all limitations\n• Removal approval\n• Device Owner stronger blocking", 14));
        root.addView(backlog);

        return pairViews;
    }

    private static LinearLayout card(Activity activity) {
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(22, 18, 22, 18);
        card.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 10, 0, 10);
        card.setLayoutParams(lp);
        return card;
    }
}
