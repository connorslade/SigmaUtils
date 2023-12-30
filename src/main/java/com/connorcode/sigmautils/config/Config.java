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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.*;


public class Config {
    public static final File configFile = new File(directory.toFile(), "config.nbt");
    static final KeyBinding configKeybinding = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("Open Gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "Sigma Utils"));
    public static HashMap<Class<Module>, List<Setting<?>>> moduleSettings = new HashMap<>();
    public static List<Setting<KeyBindSetting>> moduleKeybinds = new ArrayList<>();

    public static void initKeybindings() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (configKeybinding.wasPressed()) client.setScreen(new ConfigGui());
            var f3 = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_F3);
            if (client.currentScreen != null || f3) return;
            for (Setting<KeyBindSetting> s : moduleKeybinds) {
                if (!((KeyBindSetting) s).pressed()) continue;

                Module module = s.getModule();
                module.enabled ^= true;
                ToggleSound.play(module.enabled);
                if (module.enabled) module.enable();
                else module.disable();
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
        var nbt = Objects.requireNonNull(NbtIo.read(configFile.toPath()));
        var version = nbt.getString("version");
        if (!version.equals("SigmaUtils " + VERSION)) {
            logger.warn("Config version mismatch, backing up config");
            var version_num = version.split(" ")[1].replace(" ", "-");
            NbtIo.write(nbt, new File(directory.toFile(), String.format("config_backup_%s.nbt", version_num)).toPath());
        }

        var modules = nbt.getCompound("modules");
        for (Module i : SigmaUtils.modules.values()) {
            i.loadConfig(modules.getCompound(i.id));
            if (i.enabled) i.enable();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void save() throws IOException {
        logger.debug("Saving config");
        NbtCompound nbt = new NbtCompound();
        nbt.putString("version", "SigmaUtils " + VERSION);

        // Add modules
        NbtCompound modules = new NbtCompound();
        for (Module i : SigmaUtils.modules.values()) modules.put(i.id, i.saveConfig());
        configFile.getParentFile().mkdirs();
        nbt.put("modules", modules);

        NbtIo.write(nbt, configFile.toPath());
    }
}
