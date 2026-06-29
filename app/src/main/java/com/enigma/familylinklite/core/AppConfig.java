package com.enigma.familylinklite.core;

public final class AppConfig {
    private AppConfig() {}

    public static final int COMMAND_PORT = 45454;
    public static final int DISCOVERY_PORT = 45455;

    public static final int PROTOCOL_VERSION = 9;
    public static final int MIN_SUPPORTED_PROTOCOL = 2;

    public static final String APP_VERSION = "2.6.3";
    public static final String DEFAULT_REPO = "enigma9q/Parental-Link";

    public static final String PREFS = "p";
    public static final String DEFAULT_PARENT_NAME = "Parent Control";

    public static final String CAPABILITIES = "ping,message,volume,usage,current_app,app_block,lock_screen,lock_timeout,device_admin,accessibility_blocking,version,command_ids,replay_protection,update_prompt,profiles,second_parent_scaffold,permission_status,volume_categories,dnd_ping_bypass,log_categories,parent_call,child_audio_message,installed_app_list,block_app_by_package,blocked_app_management,child_permission_setup,travel_mode_remove_limits,child_master_password_edit,blocked_app_attention_overlay,limitation_state_modes,strict_volume_enforcement,sticky_mute_enforcement,mute_restore_previous_volume";
}
