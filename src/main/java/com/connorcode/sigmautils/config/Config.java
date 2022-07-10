package com.connorcode.sigmautils.config;

import com.connorcode.sigmautils.SigmaUtilsClient;
import com.connorcode.sigmautils.module.Module;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class Config {
    public static final File configFile = new File(MinecraftClient.getInstance().runDirectory,
            "config/SigmaUtils/config.nbt");
    static final KeyBinding configKeybinding = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.sigma-utils.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U,
                    "key.category.sigma-utils"));

    public static void initKeybindings() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (configKeybinding.wasPressed()) client.setScreen(new ConfigGui());
        });
    }

    public static boolean getEnabled(String id) throws Exception {
        Optional<Module> find = Arrays.stream(SigmaUtilsClient.modules)
                .filter(m -> Objects.equals(m.id, id))
                .findFirst();
        if (find.isEmpty()) throw new Exception("Invalid Module Id");
        return find.get().enabled;
    }

    public static void load() throws IOException {
        if (!configFile.exists()) return;
        NbtCompound nbt = NbtIo.read(configFile);
        if (nbt == null) return;
        for (Module i : SigmaUtilsClient.modules) {
            i.loadConfig(nbt.getCompound(i.id));
            if (i.enabled) i.enable(MinecraftClient.getInstance());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void save() throws IOException {
        NbtCompound nbt = new NbtCompound();
        for (Module i : SigmaUtilsClient.modules) nbt.put(i.id, i.saveConfig());
        configFile.getParentFile()
                .mkdirs();
        NbtIo.write(nbt, configFile);
    }
}
