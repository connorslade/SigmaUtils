package com.connorcode.sigmautils;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.modules.*;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class SigmaUtilsClient implements ClientModInitializer {
    public static Module[] modules = new Module[]{
            new BetterSplashes(),
            new BlockDistance(),
            new CameraClip(),
            new CameraDistance(),
            new CoordinatesHud(),
            new Deadmau5Ears(),
            new DisableFrontPerspective(),
            new DisableShadows(),
            new FlippedEntities(),
            new FullBright(),
            new NoBreakParticles(),
            new NoFog(),
            new NoParticles(),
            new Padding(),
            new PrintDeathCords(),
            new RandomBackground(),
            new ShowInvisibleEntities(),
            new TickSpeed(),
            new Zoom()
    };

    @Override
    public void onInitializeClient() {
        LogUtils.getLogger()
                .info("Starting Sigma Utils");
        Config.initKeybindings();
        ClientLifecycleEvents.CLIENT_STARTED.register((client -> {
            try {
                Config.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
}
