package com.connorcode.sigmautils.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;

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

    public void loadConfig(NbtCompound config) {
    }

    public NbtCompound saveConfig() {
        return new NbtCompound();
    }

    public void enable(MinecraftClient client) {
    }

    public void disable(MinecraftClient client) {
    }
}
