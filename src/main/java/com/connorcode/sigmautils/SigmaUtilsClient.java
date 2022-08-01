package com.connorcode.sigmautils;

import com.connorcode.sigmautils.commands.Command;
import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.module.Module;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.util.JsonHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class SigmaUtilsClient implements ClientModInitializer {
    public static final String version = "0.1";
    public static List<Module> modules = new ArrayList<>();
    public static List<Command> commands = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        LogUtils.getLogger()
                .info("Starting Sigma Utils");

        // Load modules
        JsonObject moduleJsonObject = JsonHelper.deserialize(Util.loadResourceString("modules.json"));
        String modulePackageName = moduleJsonObject.get("package")
                .getAsString();
        moduleJsonObject.get("modules")
                .getAsJsonArray()
                .forEach(json -> modules.add((Module) Util.loadNewClass(modulePackageName + "." + json.getAsString())));

        JsonObject commandJsonObject = JsonHelper.deserialize(Util.loadResourceString("commands.json"));
        String commandPackageName = commandJsonObject.get("package")
                .getAsString();
        commandJsonObject.get("commands")
                .getAsJsonArray()
                .forEach(json -> commands.add(
                        (Command) Util.loadNewClass(commandPackageName + "." + json.getAsString())));

        // Init modules
        Config.initKeybindings();
        for (Module i : modules) i.init();

        // Init Commands
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> commands
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
