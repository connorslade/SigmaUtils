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
import net.minecraft.util.Pair;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Config {
    public static final File configFile =
            new File(MinecraftClient.getInstance().runDirectory, "config/SigmaUtils/config.nbt");
    static final KeyBinding configKeybinding = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("Open Gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "Sigma Utils"));

    public static void initKeybindings() {
        List<Pair<String, KeyBinding>> moduleKeybindings = new ArrayList<>();
        for (Module i : SigmaUtilsClient.modules) {
            moduleKeybindings.add(new Pair<>(i.id, KeyBindingHelper.registerKeyBinding(
                    new KeyBinding(String.format("Toggle %s", i.name), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
                            "Sigma Utils"))));
        }

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (configKeybinding.wasPressed()) client.setScreen(new ConfigGui());
            for (Pair<String, KeyBinding> i : moduleKeybindings) {
                if (!i.getRight()
                        .wasPressed()) continue;

                Optional<Module> find = Arrays.stream(SigmaUtilsClient.modules)
                        .filter(m -> Objects.equals(m.id, i.getLeft()))
                        .findFirst();
                if (find.isEmpty()) continue;
                Module module = find.get();

                module.enabled ^= true;
                if (module.enabled) module.enable(MinecraftClient.getInstance());
                else module.disable(MinecraftClient.getInstance());
                try {
                    save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
