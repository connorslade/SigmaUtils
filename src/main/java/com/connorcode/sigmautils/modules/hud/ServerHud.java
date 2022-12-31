package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;

public class ServerHud extends HudModule {
    public ServerHud() {
        super("server_hud", "Server Hud", "Shows the server you are currently on in the HUD", Category.Hud);
        this.defaultTextColor = TextStyle.Color.Gold;
        this.defaultOrder = 7;
    }

    public String line() {
        MinecraftClient client = MinecraftClient.getInstance();
        ServerInfo serverEntry = client.getCurrentServerEntry();

        return String.format("§r%sServer: §f%s", this.getTextColor(),
                serverEntry == null ? "Integrated Server" : serverEntry.address);
    }
}
