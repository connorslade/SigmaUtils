package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.Objects;
import java.util.Optional;

public class BiomeHud extends HudModule {
    public BiomeHud() {
        super("biome_hud", "Biome Hud", "Shows the biome you are currently located within on the HUD", Category.Hud);
    }

    public String line() {
        return String.format("§r§fBiome: %s", getBiomeName());
    }

    String getBiomeName() {
        MinecraftClient client = MinecraftClient.getInstance();
        RegistryEntry<Biome> biome = Objects.requireNonNull(client.world)
                .getBiome(Objects.requireNonNull(client.player)
                        .getBlockPos());
        if (biome.getKey()
                .isEmpty()) return "?";


        Optional<RegistryKey<Biome>> biomeKey = biome.getKey();
        if (biomeKey.isEmpty()) return "??";

        return String.valueOf(biomeKey.get().getValue());
    }
}
