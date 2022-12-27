package com.connorcode.sigmautils.module;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.config.ModuleConfigGui;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.modules.meta.ToggleNotifications;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.List;

public abstract class Module {
    public final String id;
    public final String name;
    public final String description;
    public final Category category;
    public boolean enabled;

    protected Module(String id, String name, String description, Category category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public void drawInterface(MinecraftClient client, Screen screen, int x, int y) {
        Components.addToggleButton(screen, this, x, y, 150, false);
    }

    public void openConfigScreen(MinecraftClient client, Screen screen) {
        client.setScreen(new ModuleConfigGui(this, screen));
    }

    public void loadConfig(NbtCompound config) {
        enabled = Util.loadEnabled(config);
        Config.moduleSettings.getOrDefault(getClass(), List.of())
                .forEach(setting -> setting.deserialize(config));
    }

    public NbtCompound saveConfig() {
        NbtCompound nbt = Util.saveEnabled(enabled);
        Config.moduleSettings.getOrDefault(getClass(), List.of())
                .forEach(setting -> setting.serialize(nbt));
        return nbt;
    }

    public void init() {
    }

    public void tick() {
    }

    public void enable(MinecraftClient client) {
        if (client.player == null) return;
        try {
            if (Config.getEnabled("toggle_notifications")) client.player.sendMessage(Text.of(String.format("§aEnabled §d%s", name)), !ToggleNotifications.display);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disable(MinecraftClient client) {
        if (client.player == null) return;
        try {
            if (Config.getEnabled("toggle_notifications")) client.player.sendMessage(Text.of(String.format("§cDisabled §d%s", name)), !ToggleNotifications.display);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
