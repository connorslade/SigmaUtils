package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.Objects;
import java.util.Optional;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class BiomeHud extends HudModule {
    public BiomeHud() {
        super("biome_hud", "Biome Hud", "Shows the biome you are currently located within on the HUD", Category.Hud);
        this.defaultTextColor = TextStyle.Color.Red;
        this.defaultOrder = 6;
    }

    public String line() {
        return String.format("§r%sBiome: §f%s", this.getTextColor(), getBiomeName());
    }

    String getBiomeName() {
        RegistryEntry<Biome> biome = Objects.requireNonNull(client.world)
                .getBiome(Objects.requireNonNull(client.player)
                        .getBlockPos());
        if (biome.getKey()
                .isEmpty()) return "?";


        Optional<RegistryKey<Biome>> biomeKey = biome.getKey();
        if (biomeKey.isEmpty()) return "??";

        return String.valueOf(biomeKey.get()
                .getValue());
    }
}
