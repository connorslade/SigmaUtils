package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class RandomBackground extends Module {
    public static final List<String> validBackgrounds = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
            SigmaUtils.class.getClassLoader()
                    .getResourceAsStream("background_blocks.txt"))))
            .lines()
            .toList();
    public static int assetIndex = -1;

    public RandomBackground() {
        super("random_background", "Random Background", "Uses random textures for the background tessellation",
                Category.Interface);
    }

    public static void setTexture() {
        RenderSystem.setShaderTexture(0, Identifier.tryParse("textures/block/" +
                RandomBackground.validBackgrounds.get(RandomBackground.assetIndex) + ".png"));
    }
}
