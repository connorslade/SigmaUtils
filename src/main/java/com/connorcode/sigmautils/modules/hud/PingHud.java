package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import java.util.Objects;

public class PingHud extends HudModule {
    public PingHud() {
        super("ping_hud", "Ping Hud", "Shows your ping", Category.Hud);
        this.defaultTextColor = TextStyle.Color.Gold;
        this.defaultOrder = 3;
    }

    public String line() {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerListEntry playerListEntry =
                Objects.requireNonNull(client.player).networkHandler.getPlayerListEntry(client.player.getUuid());
        return String.format("§r%sPing: §f%s", this.getTextColor(),
                playerListEntry == null ? "" : playerListEntry.getLatency());
    }
}
