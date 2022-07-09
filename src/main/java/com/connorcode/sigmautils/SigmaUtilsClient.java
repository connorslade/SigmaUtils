package com.connorcode.sigmautils;

import com.connorcode.sigmautils.config.Config;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SigmaUtilsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Config.initKeybindings();
    }
}
