package com.enigma.familylinklite.protocol;

import com.enigma.familylinklite.core.AppConfig;
import org.json.JSONObject;
import java.util.UUID;

public final class CommandPacket {
    private CommandPacket() {}

    public static final String CAPABILITIES = "ping,message,volume,usage,current_app,app_block,lock_screen,lock_timeout,accessibility_blocking,device_admin,logs,version_check,update_prompt,profiles,second_parent_scaffold";

    public static JSONObject create(String type, String value, String senderName, String senderId) throws Exception {
        JSONObject j = new JSONObject();
        j.put("id", UUID.randomUUID().toString());
        j.put("time", System.currentTimeMillis());
        j.put("protocol", AppConfig.PROTOCOL_VERSION);
        j.put("minProtocol", AppConfig.MIN_SUPPORTED_PROTOCOL);
        j.put("appVersion", AppConfig.APP_VERSION);
        j.put("capabilities", CAPABILITIES);
        j.put("sender", senderName);
        j.put("senderId", senderId);
        j.put("type", type);
        j.put("value", value);
        return j;
    }
}
