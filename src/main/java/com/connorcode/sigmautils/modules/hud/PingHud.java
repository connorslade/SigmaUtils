package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.config.ModuleInfo;
import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.module.HudModule;
import net.minecraft.client.network.PlayerListEntry;

import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;

@ModuleInfo(description = "Shows your ping on the HUD")
public class PingHud extends HudModule {
    public PingHud() {
        super();
        this.defaultTextColor = TextStyle.Color.Gold;
        this.defaultOrder = 3;
    }

    public String line() {
        PlayerListEntry playerListEntry =
                Objects.requireNonNull(client.player).networkHandler.getPlayerListEntry(client.player.getUuid());
        return String.format("§r%sPing: §f%s", this.getTextColor(),
                playerListEntry == null ? "" : playerListEntry.getLatency());
    }
}
