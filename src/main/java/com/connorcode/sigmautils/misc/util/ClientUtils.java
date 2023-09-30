package com.connorcode.sigmautils.misc.util;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class ClientUtils {
    public static boolean isConnectedToRealms() {
        return client.getCurrentServerEntry() != null && client.getCurrentServerEntry().isLocal();
    }
}
