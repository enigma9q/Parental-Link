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
import com.enigma.familylinklite.R;
import com.enigma.familylinklite.ui.UiFactory;

public final class ChildHomeScreen {
    private ChildHomeScreen() {}

    public static final class PairViews {
        public TextView code;
        public TextView countdown;
        public ImageView qr;
        public android.widget.ProgressBar expiry;
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
            View.OnClickListener openMenu,
            String childIcon,
            View.OnClickListener changeIcon
    ) {
        PairViews pairViews = new PairViews();

        if (connected) {
            LinearLayout statusCard = card(activity);
            LinearLayout row = new LinearLayout(activity);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            android.widget.FrameLayout avatarWrap = new android.widget.FrameLayout(activity);
            TextView avatar = UiFactory.text(activity, childIcon == null || childIcon.length() == 0 ? "\uD83D\uDCFA" : childIcon, 30);
            avatar.setGravity(Gravity.CENTER);
            android.graphics.drawable.GradientDrawable av = new android.graphics.drawable.GradientDrawable();
            av.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            av.setColor(UiFactory.isDark(activity) ? Color.rgb(37, 54, 76) : Color.rgb(212, 230, 255));
            avatar.setBackground(av);
            avatarWrap.addView(avatar, new android.widget.FrameLayout.LayoutParams(UiFactory.dp(activity, 70), UiFactory.dp(activity, 70), Gravity.CENTER));
            TextView edit = UiFactory.text(activity, "\u270E", 16);
            edit.setGravity(Gravity.CENTER);
            edit.setTextColor(Color.WHITE);
            edit.setTypeface(Typeface.DEFAULT_BOLD);
            edit.setBackground(UiFactory.rounded(activity, UiFactory.blue(), 14));
            android.widget.FrameLayout.LayoutParams editLp = new android.widget.FrameLayout.LayoutParams(UiFactory.dp(activity, 28), UiFactory.dp(activity, 28), Gravity.RIGHT | Gravity.BOTTOM);
            avatarWrap.addView(edit, editLp);
            avatarWrap.setOnClickListener(changeIcon);
            row.addView(avatarWrap, new LinearLayout.LayoutParams(UiFactory.dp(activity, 76), UiFactory.dp(activity, 76)));

            LinearLayout info = new LinearLayout(activity);
            info.setOrientation(LinearLayout.VERTICAL);
            TextView title = UiFactory.text(activity, "Child tablet", 22);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            info.addView(title);
            info.addView(UiFactory.mutedText(activity, "Connected to " + parentName, 14));
            info.addView(UiFactory.mutedText(activity, "Ready", 14));
            info.addView(UiFactory.text(activity, "Connected", 15));
            row.addView(info, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            statusCard.addView(row);
            root.addView(statusCard);
        }

        if (connected) {
            GridLayout quick = new GridLayout(activity);
            quick.setColumnCount(4);
            quick.setPadding(0, UiFactory.dp(activity, 10), 0, UiFactory.dp(activity, 8));
            String[] icons = new String[]{"ic_proto_bell", chatUnread ? "ic_proto_bell" : "ic_proto_chat", "ic_menu_settings", "ic_proto_more"};
            String[] labels = new String[]{"Ask parent", "Quick message", "Options", "Menu"};
            View.OnClickListener[] listeners = new View.OnClickListener[]{askParent, callParent, unlockSettings, openMenu};
            for (int i = 0; i < labels.length; i++) {
                LinearLayout tile = actionTile(activity, icons[i], labels[i]);
                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.width = 0;
                lp.height = UiFactory.dp(activity, 76);
                lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                lp.setMargins(UiFactory.dp(activity, 3), UiFactory.dp(activity, 5), UiFactory.dp(activity, 3), UiFactory.dp(activity, 5));
                quick.addView(tile, lp);
                final View.OnClickListener listener = listeners[i];
                tile.setOnClickListener(listener);
            }
            root.addView(quick);
        }

        if (!connected || showPairingDetails) {
            LinearLayout pairing = card(activity);
            TextView pairTitle = UiFactory.text(activity, connected ? "Pairing details" : "Pair this tablet", 18);
            pairTitle.setTypeface(Typeface.DEFAULT_BOLD);
            pairing.addView(pairTitle);
            pairViews.code = UiFactory.text(activity, "--- ---", 34);
            pairViews.code.setGravity(Gravity.CENTER);
            pairing.addView(pairViews.code);
            pairViews.countdown = UiFactory.mutedText(activity, "IP: -- - Code refreshes automatically", 14);
            pairViews.countdown.setGravity(Gravity.CENTER);
            pairing.addView(pairViews.countdown);
            pairViews.expiry = new android.widget.ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal);
            pairViews.expiry.setMax(300);
            pairing.addView(pairViews.expiry, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UiFactory.dp(activity, 10)));
            pairViews.qr = new ImageView(activity);
            pairViews.qr.setAdjustViewBounds(true);
            LinearLayout.LayoutParams qlp = new LinearLayout.LayoutParams(UiFactory.dp(activity, 220), UiFactory.dp(activity, 220));
            qlp.gravity = Gravity.CENTER_HORIZONTAL;
            pairing.addView(pairViews.qr, qlp);
            pairing.addView(UiFactory.mutedText(activity, connected ? "Pairing details are normally hidden after setup." : "Show this code or QR to a parent phone.", 14));
            root.addView(pairing);
        }

        return pairViews;
    }

    private static LinearLayout actionTile(Activity activity, String icon, String label) {
        LinearLayout tile = new LinearLayout(activity);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER);
        tile.setPadding(UiFactory.dp(activity, 4), UiFactory.dp(activity, 5), UiFactory.dp(activity, 4), UiFactory.dp(activity, 5));
        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setColor(UiFactory.panel(activity));
        bg.setCornerRadius(UiFactory.dp(activity, 8));
        bg.setStroke(UiFactory.dp(activity, 1), UiFactory.border(activity));
        tile.setBackground(bg);
        ImageView iconView = new ImageView(activity);
        int res = activity.getResources().getIdentifier(icon, "drawable", activity.getPackageName());
        iconView.setImageResource(res != 0 ? res : R.drawable.ic_proto_question);
        iconView.setColorFilter(UiFactory.blue());
        iconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        iconView.setPadding(UiFactory.dp(activity, 2), UiFactory.dp(activity, 2), UiFactory.dp(activity, 2), UiFactory.dp(activity, 2));
        TextView labelView = UiFactory.text(activity, label, 12);
        labelView.setGravity(Gravity.CENTER);
        labelView.setMaxLines(2);
        tile.addView(iconView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UiFactory.dp(activity, 36)));
        tile.addView(labelView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return tile;
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
