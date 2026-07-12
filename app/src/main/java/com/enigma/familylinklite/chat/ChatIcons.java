package com.enigma.familylinklite.chat;

import com.enigma.familylinklite.R;

public final class ChatIcons {
    private ChatIcons() {}

    public static int res(String key) {
        if (key == null) return R.drawable.ic_proto_chat;
        String k = key.replace("icon:", "").trim().toLowerCase(java.util.Locale.US);
        if ("food".equals(k)) return R.drawable.ic_chat_food;
        if ("clock".equals(k) || "time".equals(k) || "in_5".equals(k)) return R.drawable.ic_chat_clock;
        if ("quiet".equals(k)) return R.drawable.ic_chat_quiet;
        if ("tidy".equals(k)) return R.drawable.ic_chat_tidy;
        if ("bath".equals(k)) return R.drawable.ic_chat_bath;
        if ("sleep".equals(k)) return R.drawable.ic_chat_sleep;
        if ("phone".equals(k) || "call".equals(k)) return R.drawable.ic_chat_phone;
        if ("yes".equals(k) || "ok".equals(k)) return R.drawable.ic_chat_yes;
        if ("no".equals(k) || "no_answer".equals(k)) return R.drawable.ic_chat_no;
        if ("help".equals(k)) return R.drawable.ic_chat_help;
        return R.drawable.ic_proto_chat;
    }

    public static String cleanKey(String key) {
        if (key == null || key.trim().length() == 0) return "chat";
        return key.replace("|", " ").replace("icon:", "").trim();
    }

    public static String titlePrefix(String key) {
        String k = cleanKey(key);
        if ("food".equalsIgnoreCase(k)) return "Food";
        if ("clock".equalsIgnoreCase(k) || "time".equalsIgnoreCase(k) || "in_5".equalsIgnoreCase(k)) return "In 5";
        if ("quiet".equalsIgnoreCase(k)) return "Quiet";
        if ("tidy".equalsIgnoreCase(k)) return "Tidy";
        if ("bath".equalsIgnoreCase(k)) return "Bath";
        if ("sleep".equalsIgnoreCase(k)) return "Sleep";
        if ("phone".equalsIgnoreCase(k) || "call".equalsIgnoreCase(k)) return "Call";
        if ("yes".equalsIgnoreCase(k) || "ok".equalsIgnoreCase(k)) return "Yes";
        if ("no".equalsIgnoreCase(k) || "no_answer".equalsIgnoreCase(k)) return "No";
        if ("help".equalsIgnoreCase(k)) return "Help";
        return "Chat";
    }
}
