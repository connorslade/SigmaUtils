package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import net.minecraft.client.MinecraftClient;

import java.util.Objects;

public class PingHud extends HudModule {
    public PingHud() {
        super("ping_hud", "Ping Hud", "Shows your ping", Category.Hud);
    }

    public String line() {
        MinecraftClient client = MinecraftClient.getInstance();
        return String.format("§r§6Ping: §f%d", Objects.requireNonNull(
                        Objects.requireNonNull(client.player).networkHandler.getPlayerListEntry(client.player.getUuid()))
                .getLatency());
    }
}
