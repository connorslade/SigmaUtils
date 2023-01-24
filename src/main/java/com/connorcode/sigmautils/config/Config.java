package com.connorcode.sigmautils.config;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.config.settings.KeyBindSetting;
import com.connorcode.sigmautils.config.settings.Setting;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.modules.meta.ToggleSound;
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
import java.util.*;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.SigmaUtils.directory;

public class Config {
    public static final File configFile = new File(directory.toFile(), "config.nbt");
    static final KeyBinding configKeybinding = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("Open Gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "Sigma Utils"));
    public static HashMap<Class<Module>, List<Setting<?>>> moduleSettings = new HashMap<>();
    public static List<Setting<KeyBindSetting>> moduleKeybinds = new ArrayList<>();

    public static void initKeybindings() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (configKeybinding.wasPressed()) client.setScreen(new ConfigGui());
            if (client.currentScreen != null) return;
            for (Setting<KeyBindSetting> s : moduleKeybinds) {
                if (!((KeyBindSetting) s).pressed()) continue;

                Module module = s.getModule();
                module.enabled ^= true;
                ToggleSound.play(module.enabled);
                if (module.enabled) module.enable(client);
                else module.disable(client);
                try {
                    save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static <T extends Module> boolean getEnabled(Class<T> moduleClass) {
        return SigmaUtils.modules.get(moduleClass).enabled;
    }

    public static void load() throws IOException {
        if (!configFile.exists()) return;
        NbtCompound nbt = Objects.requireNonNull(NbtIo.read(configFile)).getCompound("modules");
        if (nbt == null) return;
        for (Module i : SigmaUtils.modules.values()) {
            i.loadConfig(nbt.getCompound(i.id));
            if (i.enabled) i.enable(client);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void save() throws IOException {
        NbtCompound nbt = new NbtCompound();

        // Add modules
        NbtCompound modules = new NbtCompound();
        for (Module i : SigmaUtils.modules.values()) modules.put(i.id, i.saveConfig());
        configFile.getParentFile().mkdirs();
        nbt.put("modules", modules);

        NbtIo.write(nbt, configFile);
    }
}
