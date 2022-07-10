package com.connorcode.sigmautils.modules;

import com.connorcode.sigmautils.Category;
import com.connorcode.sigmautils.Module;
import com.connorcode.sigmautils.SigmaUtilsClient;
import net.minecraft.nbt.NbtCompound;

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

    public RandomBackground() {
        super("random_background", "Random Background", "Uses random textures for the background tessellation",
                Category.Interface);
    }

    public void loadConfig(NbtCompound config) {
        enabled = config.getBoolean("enabled");
    }

    public NbtCompound saveConfig() {
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("enabled", enabled);
        return nbt;
    }
}
