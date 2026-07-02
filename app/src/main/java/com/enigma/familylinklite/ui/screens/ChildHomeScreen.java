package com.enigma.familylinklite.ui.screens;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.enigma.familylinklite.ui.UiFactory;

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
            boolean chatUnread,
            View.OnClickListener askParent,
            View.OnClickListener openLanguage,
            View.OnClickListener openHelp,
            View.OnClickListener openMenu
    ) {
        PairViews pairViews = new PairViews();

        LinearLayout statusCard = card(activity);
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        TextView avatar = UiFactory.text(activity, "👦", 34);
        avatar.setGravity(Gravity.CENTER);
        android.graphics.drawable.GradientDrawable av = new android.graphics.drawable.GradientDrawable();
        av.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        av.setColor(UiFactory.isDark(activity) ? Color.rgb(37, 54, 76) : Color.rgb(212, 230, 255));
        avatar.setBackground(av);
        row.addView(avatar, new LinearLayout.LayoutParams(UiFactory.dp(activity, 70), UiFactory.dp(activity, 70)));

        LinearLayout info = new LinearLayout(activity);
        info.setOrientation(LinearLayout.VERTICAL);
        TextView title = UiFactory.text(activity, "Child tablet", 22);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        info.addView(title);
        info.addView(UiFactory.mutedText(activity, connected ? "Parent linked: " + parentName : "Waiting for parent pairing", 14));
        info.addView(UiFactory.mutedText(activity, permissionsOk ? "Permissions OK" : "Permissions need review", 14));
        info.addView(UiFactory.text(activity, connected ? "Ready" : "Pairing needed", 15));
        row.addView(info, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        statusCard.addView(row);
        root.addView(statusCard);

        LinearLayout request = card(activity);
        TextView requestTitle = UiFactory.text(activity, "Requests", 18);
        requestTitle.setTypeface(Typeface.DEFAULT_BOLD);
        request.addView(requestTitle);
        request.addView(UiFactory.mutedText(activity, "Requests will be visible here.", 14));
        root.addView(request);

        GridLayout quick = new GridLayout(activity);
        quick.setColumnCount(4);
        quick.setPadding(0, UiFactory.dp(activity, 10), 0, UiFactory.dp(activity, 8));
        String[] labels = new String[]{"❔\nAsk\nParent", chatUnread ? "💬\nQuick\nmessage •" : "💬\nQuick\nmessage", "＋\nAdd\naction", "＋\nAdd\naction", "🛡️\nCheck\npermissions", "🌐\nLanguage", "?\nHelp", "⋯\nMenu"};
        View.OnClickListener[] listeners = new View.OnClickListener[]{askParent, callParent, callParent, callParent, unlockSettings, openLanguage, openHelp, openMenu};
        for (int i = 0; i < labels.length; i++) {
            Button b = UiFactory.button(activity, labels[i]);
            b.setTextSize(12);
            b.setGravity(Gravity.CENTER);
            int w = (activity.getResources().getDisplayMetrics().widthPixels - UiFactory.dp(activity, 52)) / 4;
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = w;
            lp.height = UiFactory.dp(activity, 96);
            lp.setMargins(UiFactory.dp(activity, 3), UiFactory.dp(activity, 5), UiFactory.dp(activity, 3), UiFactory.dp(activity, 5));
            quick.addView(b, lp);
            final View.OnClickListener listener = listeners[i];
            b.setOnClickListener(listener);
        }
        root.addView(quick);

        if (!connected || showPairingDetails) {
            LinearLayout pairing = card(activity);
            TextView pairTitle = UiFactory.text(activity, connected ? "Pairing details" : "Pair this tablet", 18);
            pairTitle.setTypeface(Typeface.DEFAULT_BOLD);
            pairing.addView(pairTitle);
            pairViews.code = UiFactory.text(activity, "--- ---", 34);
            pairViews.code.setGravity(Gravity.CENTER);
            pairing.addView(pairViews.code);
            pairViews.countdown = UiFactory.mutedText(activity, "IP: -- • Code refreshes automatically", 14);
            pairViews.countdown.setGravity(Gravity.CENTER);
            pairing.addView(pairViews.countdown);
            pairViews.qr = new ImageView(activity);
            pairViews.qr.setAdjustViewBounds(true);
            LinearLayout.LayoutParams qlp = new LinearLayout.LayoutParams(UiFactory.dp(activity, 220), UiFactory.dp(activity, 220));
            qlp.gravity = Gravity.CENTER_HORIZONTAL;
            pairing.addView(pairViews.qr, qlp);
            pairing.addView(UiFactory.mutedText(activity, connected ? "Pairing details are normally hidden after setup." : "Show this code or QR to a parent phone.", 14));
            root.addView(pairing);
        }

        LinearLayout activityCard = card(activity);
        TextView activityTitle = UiFactory.text(activity, "Activity & status", 18);
        activityTitle.setTypeface(Typeface.DEFAULT_BOLD);
        activityCard.addView(activityTitle);
        activityCard.addView(UiFactory.mutedText(activity, "Ask Parent — Ready", 14));
        activityCard.addView(UiFactory.mutedText(activity, "Quick messages — Ready", 14));
        activityCard.addView(UiFactory.mutedText(activity, permissionsOk ? "Permissions — OK" : "Permissions — Check needed", 14));
        root.addView(activityCard);

        return pairViews;
    }

    private static LinearLayout card(Activity activity) {
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(UiFactory.dp(activity, 12), UiFactory.dp(activity, 12), UiFactory.dp(activity, 12), UiFactory.dp(activity, 12));
        card.setBackground(UiFactory.rounded(activity, UiFactory.panel(activity), 18));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, UiFactory.dp(activity, 10), 0, UiFactory.dp(activity, 4));
        card.setLayoutParams(lp);
        return card;
    }
}
