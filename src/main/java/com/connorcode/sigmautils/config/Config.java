package com.connorcode.sigmautils.config;

import com.connorcode.sigmautils.SigmaUtilsClient;
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
import net.minecraft.util.Pair;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Config {
    public static final File configFile =
            new File(MinecraftClient.getInstance().runDirectory, "config/SigmaUtils/config.nbt");
    static final KeyBinding configKeybinding = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("Open Gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "Sigma Utils"));
    public static HashMap<Class<Module>, List<Setting>> moduleSettings = new HashMap<>();

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

                Optional<Module> find = SigmaUtilsClient.modules.stream()
                        .filter(m -> Objects.equals(m.id, i.getLeft()))
                        .findFirst();
                if (find.isEmpty()) continue;
                Module module = find.get();

                module.enabled ^= true;
                ToggleSound.play(module.enabled);
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

    public static boolean getEnabled(String id) {
        Optional<Module> find = SigmaUtilsClient.modules.stream()
                .filter(m -> Objects.equals(m.id, id))
                .findFirst();
        return find.isPresent() && find.get().enabled;
    }

    public static <T extends Module> boolean getEnabled(Class<T> moduleClass) {
        return getEnabled(getModule(moduleClass).id);
    }

    public static <T extends Module> Module getModule(Class<T> moduleClass) {
        try {
            return moduleClass.getConstructor()
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("Invalid module class `%s`", moduleClass.getName()));
        }
    }

    public static void load() throws IOException {
        if (!configFile.exists()) return;
        NbtCompound nbt = Objects.requireNonNull(NbtIo.read(configFile))
                .getCompound("modules");
        if (nbt == null) return;
        for (Module i : SigmaUtilsClient.modules) {
            i.loadConfig(nbt.getCompound(i.id));
            if (i.enabled) i.enable(MinecraftClient.getInstance());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void save() throws IOException {
        NbtCompound nbt = new NbtCompound();

        // Add modules
        NbtCompound modules = new NbtCompound();
        for (Module i : SigmaUtilsClient.modules) modules.put(i.id, i.saveConfig());
        configFile.getParentFile()
                .mkdirs();
        nbt.put("modules", modules);

        NbtIo.write(nbt, configFile);
    }
}
