package com.connorcode.sigmautils.modules;

import com.connorcode.sigmautils.config.Config;

public class RandomBackground extends Module{
    protected RandomBackground() {
        super("Random Background", "Uses random textures for the background tessellation", Category.Interface);
    }

    @Override
    public boolean getConfig() {
        return Config.randomBackground;
    }

    @Override
    public void setConfig(boolean config) {
        Config.randomBackground = config;
    }
}
