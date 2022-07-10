package com.connorcode.sigmautils.modules;

import com.connorcode.sigmautils.config.Config;
import net.minecraft.client.MinecraftClient;

public class NoFog extends Module{
    protected NoFog() {
        super("No Fog", "Removes fog rendering", Category.World);
    }

    @Override
    public boolean getConfig() {
        return Config.noFog;
    }

    @Override
    public void setConfig(boolean config) {
        Config.noFog = config;
    }

    @Override
    public void enable(MinecraftClient client) {}

    @Override
    public void disable(MinecraftClient client) {}
}
