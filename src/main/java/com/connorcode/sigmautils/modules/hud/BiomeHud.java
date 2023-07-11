package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.config.ModuleInfo;
import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.module.HudModule;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;

import java.util.Objects;
import java.util.Optional;

import static com.connorcode.sigmautils.SigmaUtils.client;

@ModuleInfo(description = "Shows the biome you are currently located within on the HUD")
public class BiomeHud extends HudModule {
    public BiomeHud() {
        super();
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
