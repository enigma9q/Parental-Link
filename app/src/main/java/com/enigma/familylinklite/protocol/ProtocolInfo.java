package com.enigma.familylinklite.protocol;

import com.enigma.familylinklite.core.AppConfig;

public final class ProtocolInfo {
    private ProtocolInfo() {}

    public static boolean isCompatible(int remoteProtocol, int remoteMinSupportedProtocol) {
        return remoteProtocol >= AppConfig.MIN_SUPPORTED_PROTOCOL
                && AppConfig.PROTOCOL_VERSION >= remoteMinSupportedProtocol;
    }
}
