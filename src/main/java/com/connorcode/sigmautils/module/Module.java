package com.connorcode.sigmautils.module;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.modules.meta.ToggleNotifications;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.Objects;

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

    public void drawConfigInterface(MinecraftClient client, Screen screen, int x, int y) {
        Components.addToggleButton(screen, this, x, y, 150, false);
    }

    public void loadConfig(NbtCompound config) {
    }

    public NbtCompound saveConfig() {
        return new NbtCompound();
    }

    public void init() {
    }

    public void enable(MinecraftClient client) {
        try {
            if (Config.getEnabled("toggle_notifications")) Objects.requireNonNull(client.player)
                    .sendMessage(Text.of(String.format("§aEnabled §d%s", name)), !ToggleNotifications.display);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disable(MinecraftClient client) {
        try {
            if (Config.getEnabled("toggle_notifications")) Objects.requireNonNull(client.player)
                    .sendMessage(Text.of(String.format("§cDisabled §d%s", name)), !ToggleNotifications.display);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
