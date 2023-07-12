package com.connorcode.sigmautils.module;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.config.ModuleConfigGui;
import com.connorcode.sigmautils.config.ModuleInfo;
import com.connorcode.sigmautils.config.settings.DummySetting;
import com.connorcode.sigmautils.config.settings.KeyBindSetting;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.modules.meta.Notifications;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static com.connorcode.sigmautils.SigmaUtils.client;

public abstract class Module {
    public final String id;
    public final String name;
    public final String description;
    public final String documentation;
    public final Category category;
    public boolean enabled;

    protected Module(String id, String name, String description, Category category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category.assertValid();
        this.documentation = null;
    }

    protected Module(String description, Category category) {
        var name = getClass().getSimpleName();
        this.name = Util.titleString(name);
        this.id = Util.toSnakeCase(this.name);
        this.description = description;
        this.category = category.assertValid();
        this.documentation = null;
    }

    protected Module() {
        var _class = getClass();
        if (!_class.isAnnotationPresent(ModuleInfo.class))
            throw new RuntimeException(String.format("Class %s is missing ModuleInfo annotation", _class.getName()));
        var info = _class.getAnnotation(ModuleInfo.class);
        this.id = info.id().equals("") ? Util.toSnakeCase(Util.titleString(_class.getSimpleName())) : info.id();
        this.name = info.name().equals("") ? Util.titleString(_class.getSimpleName()) : info.name();
        this.description = info.description();
        this.category =
                info.category() == Category.Unset ? Category.guessCategory(_class).assertValid() : info.category();
        this.documentation = info.documentation().equals("") ? null : info.documentation();
    }

    protected void info(String format, Object... args) {
        log(format, false, args);
    }

    protected void error(String format, Object... args) {
        log(format, true, args);
    }

    private void log(String format, boolean error, Object... args) {
        var fmt = String.format(format, args);
        var text = String.format("[SigmaUtils::%s] %s%s", name.replace(" ", ""), error ? "§c" : "", fmt);

        if (client.player == null) (error ? System.err : System.out).println(text);
        else client.player.sendMessage(Text.of(text));
    }

    public void drawInterface(MinecraftClient client, Screen screen, int x, int y) {
        Components.addToggleButton(screen, this, x, y, 150, false);
    }

    public void openConfigScreen(MinecraftClient client, Screen screen) {
        client.setScreen(new ModuleConfigGui(this, screen));
    }

    public void loadConfig(NbtCompound config) {
        enabled = config.getBoolean("enabled");
        Config.moduleSettings.getOrDefault(getClass(), List.of())
                .forEach(setting -> setting.deserialize(config));
    }

    public NbtCompound saveConfig() {
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("enabled", enabled);
        Config.moduleSettings.getOrDefault(getClass(), List.of())
                .forEach(setting -> setting.serialize(nbt));
        return nbt;
    }

    public void init() {
        Config.moduleSettings.putIfAbsent((Class<Module>) this.getClass(), new ArrayList<>());
        KeyBindSetting keyBind = new KeyBindSetting(this.getClass(), "Keybind").description(
                "The keybind for toggling module. Right click to en/disable strict mode, which requires the modifier keys not present in the keybind to be depressed.");
        Config.moduleSettings.get(this.getClass()).add(0, new DummySetting(this.getClass(), "Enable", 20));
        Config.moduleSettings.get(this.getClass()).add(1, keyBind);
        Config.moduleKeybinds.add(keyBind);
    }

    public void enable() {
        this.enabled = true;
        if (client.player == null || !Config.getEnabled(Notifications.class)) return;
        Notifications.moduleEnable(client, this);
    }

    public void disable() {
        this.enabled = false;
        if (client.player == null || !Config.getEnabled(Notifications.class)) return;
        Notifications.moduleDisable(client, this);
    }
}
