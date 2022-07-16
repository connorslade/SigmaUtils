package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.Objects;

public class CoordinatesHud extends HudModule {
    public CoordinatesHud() {
        super("coordinates_hud", "Coordinates Hud", "Adds a display on your screen showing your current location",
                Category.Hud);
    }

    @Override
    public String line() {
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        return String.format("§r§ePos: §f%d, %d, %d", (int) Math.round(player.getX()), (int) Math.round(player.getY()),
                (int) Math.round(player.getZ()));
    }
}
