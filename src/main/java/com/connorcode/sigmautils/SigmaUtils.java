package com.connorcode.sigmautils;

import com.connorcode.sigmautils.commands.Command;
import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.modules.meta.Notifications;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class SigmaUtils implements ClientModInitializer {
    public static final String VERSION = "0.1.3 alpha";
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static final Logger logger = LogUtils.getLogger();

    public static final Path directory = client.runDirectory.toPath().resolve("config/SigmaUtils");
    public static final HashMap<Class<? extends Module>, Module> modules = new HashMap<>();
    public static final List<Command> commands = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        logger.info("Starting Sigma Utils v" + VERSION);

        // Load modules
        JsonObject moduleJsonObject = JsonHelper.deserialize(Util.loadResourceString("modules/modules.json"));
        String modulePackageName = moduleJsonObject.get("package").getAsString();
        moduleJsonObject.get("modules").getAsJsonArray().forEach(json -> {
            Module module = (Module) Util.loadNewClass(modulePackageName + "." + json.getAsString());
            modules.put(Objects.requireNonNull(module).getClass(), module);
        });
        logger.debug(String.format("Loaded %d modules", modules.size()));

        // Load Commands
        JsonObject commandJsonObject = JsonHelper.deserialize(Util.loadResourceString("modules/commands.json"));
        String commandPackageName = commandJsonObject.get("package").getAsString();
        commandJsonObject.get("commands")
                .getAsJsonArray()
                .forEach(json -> commands.add(
                        (Command) Util.loadNewClass(commandPackageName + "." + json.getAsString())));
        logger.debug(String.format("Loaded %d commands", commands.size()));

        // Init modules
        Config.initKeybindings();
        for (Module i : modules.values()) i.init();

        // Init Commands
        ClientCommandRegistrationCallback.EVENT.register(
                ((dispatcher, registryAccess) -> commands.forEach(c -> c.register(dispatcher))));

        // Load config
        ClientLifecycleEvents.CLIENT_STARTED.register((client -> {
            try {
                Config.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Notifications.onStartup();
        }));
    }
}