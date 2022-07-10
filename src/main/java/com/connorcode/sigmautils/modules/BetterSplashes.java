package com.connorcode.sigmautils.modules;

import com.connorcode.sigmautils.SigmaUtilsClient;
import com.connorcode.sigmautils.config.Config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class BetterSplashes extends Module {
    public static List<String> betterSplashes = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
            SigmaUtilsClient.class.getClassLoader()
                    .getResourceAsStream("splashes.txt")))).lines()
            .toList();

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
