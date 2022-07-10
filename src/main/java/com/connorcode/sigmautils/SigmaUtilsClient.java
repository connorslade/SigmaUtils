package com.connorcode.sigmautils;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.modules.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class SigmaUtilsClient implements ClientModInitializer {
    public static Module[] modules = new Module[]{
            new BetterSplashes(),
            new FullBright(),
            new NoBreakParticles(),
            new NoFog(),
            new NoParticles(),
            new PrintDeathCords(),
            new RandomBackground(),
            };

    @Override
    public void onInitializeClient() {
        Config.initKeybindings();
        try {
            Config.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
