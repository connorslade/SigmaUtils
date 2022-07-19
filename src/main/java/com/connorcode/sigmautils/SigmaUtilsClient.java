package com.connorcode.sigmautils;

import com.connorcode.sigmautils.commands.About;
import com.connorcode.sigmautils.commands.Command;
import com.connorcode.sigmautils.commands.Toggle;
import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.modules._interface.*;
import com.connorcode.sigmautils.modules.hud.*;
import com.connorcode.sigmautils.modules.meta.*;
import com.connorcode.sigmautils.modules.misc.*;
import com.connorcode.sigmautils.modules.rendering.*;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

import java.io.IOException;
import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class SigmaUtilsClient implements ClientModInitializer {
    public static final String version = "0.1";
    public static Command[] commands = new Command[]{
            new About(),
            new Toggle()
    };
    public static Module[] modules = new Module[]{
            new BetterSplashes(),
            new BiomeHud(),
            new BlockDistance(),
            new CameraClip(),
            new CameraDistance(),
            new ChatMessageDing(),
            new ChatPosition(),
            new CoordinatesHud(),
            new Deadmau5Ears(),
            new DisableFrontPerspective(),
            new DisableShadows(),
            new EffectHud(),
            new FlippedEntities(),
            new FpsHud(),
            new FullBright(),
            new GlowingPlayers(),
            new HotbarPosition(),
            new Hud(),
            new NoArmor(),
            new NoBossBarValue(),
            new NoBreakParticles(),
            new NoChatFade(),
            new NoFog(),
            new NoGlobalSounds(),
            new NoHurtTilt(),
            new NoParticles(),
            new NoScoreboardValue(),
            new NoTelemetry(),
            new Padding(),
            new PrintDeathCords(),
            new RandomBackground(),
            new ServerHud(),
            new ShowInvisibleEntities(),
            new TickSpeed(),
            new ToggleNotifications(),
            new WatermarkHud(),
            new Zoom()
    };

    @Override
    public void onInitializeClient() {
        LogUtils.getLogger()
                .info("Starting Sigma Utils");
        Config.initKeybindings();

        // Init modules
        for (Module i : modules) i.init();

        // Init Commands
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> Arrays.stream(commands)
                .forEach(c -> c.register(dispatcher))));

        // Load config
        ClientLifecycleEvents.CLIENT_STARTED.register((client -> {
            try {
                Config.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
}
