package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class RandomBackground extends Module {
    static final List<String> validBackgrounds = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
            SigmaUtils.class.getClassLoader().getResourceAsStream("assets/sigma-utils/background_blocks.txt")))).lines()
            .toList();
    static int assetIndex = -1;
    static int screenHash = -1;


    public RandomBackground() {
        super("random_background", "Random Background", "Uses random textures for the background tessellation",
                Category.Interface);
    }

    public static Identifier getTexture() {
        int currentScreenHash = Objects.requireNonNull(Objects.requireNonNull(client).currentScreen).hashCode();

        if (screenHash != currentScreenHash || assetIndex < 0) {
            screenHash = currentScreenHash;
            assetIndex = new Random().nextInt(RandomBackground.validBackgrounds.size());
        }

        return Identifier.tryParse("textures/block/" + RandomBackground.validBackgrounds.get(assetIndex) + ".png");
    }
    // If you dare try to make backgrounds selectable again:
    // https://gist.github.com/Basicprogrammer10/2c0b3f0dd815e93cd64abe4bf4810511
}
