package com.connorcode.sigmautils.modules;

import net.minecraft.client.MinecraftClient;

public abstract class Module {
    public final String name;
    public final String description;
    public final Category category;

    protected Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public boolean getConfig() { return false; }
    public void setConfig(boolean config) {}
    public void enable(MinecraftClient client) {}
    public void disable(MinecraftClient client) {}
}
