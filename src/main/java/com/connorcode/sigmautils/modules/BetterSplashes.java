package com.connorcode.sigmautils.modules;

import com.connorcode.sigmautils.config.Config;

public class BetterSplashes extends Module{
    protected BetterSplashes() {
        super("Better Splashes", "Adds alot of custom splashes :)", Category.Interface);
    }

    @Override
    public boolean getConfig() {
        return Config.betterSplashes;
    }

    @Override
    public void setConfig(boolean config) {
        Config.betterSplashes = config;
    }
}
