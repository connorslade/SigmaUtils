package com.connorcode.sigmautils.modules;

import com.connorcode.sigmautils.SigmaUtilsClient;
import com.connorcode.sigmautils.config.Config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class RandomBackground extends Module {
    public static List<String> validBackgrounds = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
            SigmaUtilsClient.class.getClassLoader()
                    .getResourceAsStream("background_blocks.txt"))))
            .lines()
            .toList();

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
